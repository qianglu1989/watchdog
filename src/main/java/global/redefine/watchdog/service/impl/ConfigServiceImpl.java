package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.redefine.nove.http.HttpClientUtils;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.event.WatchEvent;
import global.redefine.watchdog.service.ApplicationConfigService;
import global.redefine.watchdog.service.ConfigService;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 此类用于处理应用配置文件更新 添加等操作
 * Created by luqiang on 2018/5/14.
 */
@Service("configService")
@RefreshScope
public class ConfigServiceImpl implements ConfigService {

    private static Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);


    @Resource
    private ApplicationConfigService applicationConfigService;

    @Resource
    private KafkaTemplate kafkaTemplate;

    @Autowired
    ApplicationContext applicationContext;


    private ExecutorService executor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(100));

    /**
     * 获取应用配置详情
     * index.html
     * static
     *
     * @param name 应用配置文件名称
     * @return
     */
    @Override
    public Map<String, String> getServiceConfig(String name) {

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String localPath = config.get("redefine.git.localPath");
        String branch = config.get("redefine.git.branch");
        String username = config.get("redefine.git.username");
        String password = config.get("redefine.git.password");
        String realPath = config.get("redefine.git.realPath");

        long start = System.currentTimeMillis();
        LOG.info("redefine.git.localPath:{},redefine.git.branch:{},redefine.git.username:{},redefine.git.password:{},redefine.git.realPath:{}", localPath, branch, username, password, realPath);
        Map<String, String> result = new LinkedHashMap<>();

        String filePath = StringUtils.isNotEmpty(realPath) ? localPath + realPath : localPath;
        File file = new File(filePath + "/" + name);
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String str = null;

            while ((str = bufferedReader.readLine()) != null) {

                int index = StringUtils.ordinalIndexOf(str, "=", 1);

                if (StringUtils.isEmpty(str) || index == -1) {
                    continue;
                }
                String key = str.substring(0, index);
                String val = str.substring(index + 1, str.length());
                result.put(key, val);
            }
            long end = System.currentTimeMillis();
            LOG.info("getServiceConfig time :{}", end - start);
            return result;
        } catch (Exception e) {
            LOG.error("getServiceConfig error:{}", e);
        }
        return result;
    }

    /**
     * 获取应用服务列表
     *
     * @return
     */
    @Override
    public List<String> getServices(boolean type) {

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String profile = config.get("profile");
        String project = config.get("project");

        String files = RedefineClusterUtils.getString("watchdog:confignames:" + project + ":" + profile);
        if (StringUtils.isEmpty(files) || type) {
            return checkConfigs(config);
        }
        return JSON.parseObject(files, List.class);
    }

    /**
     * 用于检测配置
     */
    private List<String> checkConfigs(Map<String, String> config) {

        String localPath = config.get("redefine.git.localPath");
        String uri = config.get("redefine.git.uri");
        String realPath = config.get("redefine.git.realPath");
        String profile = config.get("profile");
        String project = config.get("project");
        String username = config.get("redefine.git.username");
        String password = config.get("redefine.git.password");
        String branch = config.get("redefine.git.branch");

        List<String> result = new LinkedList<>();
        boolean flag = GitUtils.pullBranchToLocal(localPath, username, password, branch);
        if (!flag) {
            GitUtils.deleteAndCreat(uri, localPath, username, password, branch);
        }
        String filePath = StringUtils.isNotEmpty(realPath) ? localPath + realPath : localPath;

        File f = new File(filePath);


        File[] files = f.listFiles();

        //兼容版本
        if (files == null) {
            File nf = new File(localPath);
            files = nf.listFiles();
        }

        for (File file : files) {
            String name = file.getName();
            result.add(name);
        }

        RedefineClusterUtils.saveString("watchdog:confignames:" + project + ":" + profile, JSON.toJSONString(result));
        return result;
    }

    /**
     * 更新配置文件
     *
     * @param name  配置文件名称
     * @param datas 存储数据 add logs
     * @return
     */
    @Override
    public boolean updateConfig(String name, Map<String, String> datas, String token) {

        if (MapUtils.isEmpty(datas)) {
            return false;
        }

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String localPath = config.get("redefine.git.localPath");
        String branch = config.get("redefine.git.branch");
        String username = config.get("redefine.git.username");
        String password = config.get("redefine.git.password");
        String realPath = config.get("redefine.git.realPath");
        String uri = config.get("redefine.git.uri");

        String msg = "user " + token + " update file " + name;

        //防御性容错，移除空key
        datas.remove("");
        HashMap<String, String> newDatas = datas.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        boolean result = false;
        try {
            String filePath = StringUtils.isNotEmpty(realPath) ? localPath + realPath : localPath;
            synchronized (this) {
                Writer fileWriter = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(filePath + "/" + name), "UTF-8"));
                for (Map.Entry<String, String> entry : newDatas.entrySet()) {
                    String value = entry.getKey() + "=" + entry.getValue();
                    fileWriter.write(value);
                    fileWriter.write("\r\n");
                }

                fileWriter.flush();

                GitUtils.commitAndPush(localPath, username, password, msg, branch);
            }
            result = true;


        } catch (Exception e) {
            LOG.error("updateConfig error:{}", e);
            GitUtils.deleteAndCreat(uri, localPath, username, password, branch);
            try {
                GitUtils.commitAndPush(localPath, username, password, msg, branch);
            } catch (Exception e1) {

            }

            result = false;
        } finally {
            executor.execute(() -> {
                checkConfigs(config);
            });
        }
        if (result) {
            //发送配置更新预警
            Map<String, String> record = new HashMap<>();
            record.put("serviceName", name);
            record.put("type", "Git Config Modify");
            record.put("user", token);
            StringBuilder builder = new StringBuilder();
            builder.append("Config [ ").append(name).append(" ] ").append("Branch: [ ").append(branch).append(" ] By User [ ").append(token).append(" ] Modify");
            record.put("details", builder.toString());

            applicationContext.publishEvent(new WatchEvent(this, record));
        }

        return result;
    }

    /**
     * 刷新服务配置
     *
     * @return
     */
    @Override
    public boolean refreshConfig() {


        Map<String, Object> apps = applicationConfigService.getApps();
        apps.forEach((k, v) -> {
            LOG.info("starting refresh:{} config", k);
            List<Object> hosts = (List<Object>) v;

            hosts.forEach(host -> {

                executor.execute(() -> {
                    try {
                        HttpClientUtils.setHttpParam(5000, 10000, 20000);
                        String req = "http://" + host + "/info";
                        String reqN = "http://" + host + "/nove-refresh/XXXX:YYYY";
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        String infoResult = HttpClientUtils.doGet(req);
                        if (infoResult.contains("abt")) {
                            HttpClientUtils.doPost(reqN, null, headers);
                            LOG.info("刷新配置:{},info msg:{}", reqN, infoResult);
                        }
                    } catch (Exception e) {
                        LOG.error("refreshConfig ABTEST error:{}", e);
                    }
                });

            });
        });


        return false;
    }

}
