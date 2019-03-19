package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.redefine.nove.http.HttpClientUtils;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.service.VersionService;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author luqiang
 */
@Service("versionService")
public class VersionServiceImpl implements VersionService {

    private static Logger LOG = LoggerFactory.getLogger(VersionServiceImpl.class);

    private static String LOCAL_PATH = "/data/redefine/code";

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("versioncheck-pool-%d").build();


    private Executor executors = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(200), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Value("${redefine.versioncheck.username:luqiang}")
    private String username;

    @Value("${redefine.versioncheck.pwd:598505651}")
    private String pwd;

    /**
     * 添加应用配置信息
     * 已过期不用于版本检测
     */
    @Override
    @Deprecated
    public boolean addVersionConfig(String appName, String gitUrl) {

        RedefineClusterUtils.hashSet("ultron:version:all", appName, gitUrl);
        return true;
    }

    /**
     * 获取应用版本列表
     * 已过期不用于版本检测
     */
    @Override
    @Deprecated
    public List<Map<Object, Object>> getVersionList() {

        List<Map<Object, Object>> result = new LinkedList<>();
        Map<Object, Object> datas = RedefineClusterUtils.hgetAll("ultron:version:details");
        datas.forEach((k, v) -> {
            Map<Object, Object> data = JSON.parseObject(String.valueOf(v), Map.class);
            result.add(data);
        });
        return result;
    }

    /**
     * 检测版本信息
     * 已过期不用于版本检测
     */
    @Override
    @Deprecated
    public boolean checkVersion(String name) {

        Map<String, Object> datas = new HashMap<>();
        Map<Object, Object> gitUrls = new HashMap<>();
        if (StringUtils.isNotEmpty(name)) {
            Object url = RedefineClusterUtils.hashGet("ultron:version:all", name);
            if (StringUtils.isEmpty(String.valueOf(url))) {
                return false;
            }
            gitUrls.put(name, url);
        } else {
            gitUrls = RedefineClusterUtils.hgetAll("ultron:version:all");

        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        String day = indDate.format(formatter);

        gitUrls.forEach((k, v) -> {

            executors.execute(() -> {
                String path = LOCAL_PATH + "/" + String.valueOf(k);
                datas.put("name", k);
                datas.put("gitUrl", v);
                try {
                    //clone git
                    GitUtils.init(path, "master", username, pwd, String.valueOf(v));
                    Files.list(Paths.get(path)).forEach(f -> {
                        if ("pom.xml".equals(f.toFile().getName())) {
                            try {
                                SAXReader reader = new SAXReader();
                                Document document = reader.read(f.toFile());
                                Element root = document.getRootElement();
                                Element appversion = root.element("version");
                                Element artifactId = root.element("artifactId");
                                Element parent = root.element("parent");
                                Element parVersion = parent.element("version");
                                Element parArtifactId = parent.element("artifactId");
                                datas.put("name", k);
                                datas.put("gitUrl", v);
                                datas.put("appversion", appversion.getText());
                                datas.put("parent", parArtifactId.getText());
                                datas.put("parVersion", parVersion.getText());
                                datas.put("date", day);
                                RedefineClusterUtils.hashSet("ultron:version:details", artifactId.getText(), JSON.toJSONString(datas));

                            } catch (DocumentException e) {

                                LOG.error("check version DocumentException:{}", e);
                            }

                        }
                    });
                } catch (Exception e) {

                    LOG.error("check version error:{}", e);
                }
            });


        });

        return true;
    }


    /**
     * 检测版本信息
     */
    @Override
    public boolean checkVersion() {

        Map<String, String> versionMsg = getBaseMsg();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
        String day = indDate.format(formatter);

        for (Map.Entry<String, String> entry : versionMsg.entrySet()) {
            executors.execute(() -> {
                try {
                    String result = HttpClientUtils.doGet("http://" + entry.getValue() + "/info", new HashMap<>());
                    Map<String, JSONObject> maps = JSON.parseObject(result, Map.class);
                    JSONObject jsonObject = maps.get("versioncheck");
                    Map<String, Object> datas = new HashMap<>();

                    if (jsonObject != null) {
                        Object version = jsonObject.get("version");
                        Object parent = jsonObject.get("parent");
                        datas.put("appversion", version);
                        datas.put("parent", parent);
                    }
                    datas.put("name", entry.getKey());

                    datas.put("date", day);
                    RedefineClusterUtils.hashSet("ultron:version:details", String.valueOf(entry.getKey()), JSON.toJSONString(datas));


                } catch (Exception e) {
                    LOG.error("获取应用版本信息出错 ,ERROR:{}", e);
                }
            });
        }

        return true;
    }


    /**
     * 获取应用基础信息
     *
     * @return
     */
    private Map<String, String> getBaseMsg() {

        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String defaultZone = config.get("redefine.eureka.defaultZone");

        Map<String, String> apps = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        try {

            String result = HttpClientUtils.doGet(defaultZone + "/apps", new HashMap<>(), "UTF-8", headers);
            Map<String, JSONObject> maps = JSON.parseObject(result, Map.class);
            JSONObject jsonObject = maps.get("applications");
            JSONArray jsonArray = (JSONArray) jsonObject.get("application");
            jsonArray.forEach(obj -> {

                JSONObject o = (JSONObject) obj;
                String name = (String) o.get("name");
                JSONArray instances = (JSONArray) o.get("instance");
                JSONObject instance = (JSONObject) instances.get(0);

                Map<String, String> data = (Map<String, String>) instance.get("metadata");
                String ip = (String) instance.get("ipAddr");
                String port = data.get("management.port");
                apps.put(name, ip + ":" + port);

            });
        } catch (Exception e) {
            LOG.error("获取应用数据列表ERROR:{}", e);
        }
        return apps;
    }
}
