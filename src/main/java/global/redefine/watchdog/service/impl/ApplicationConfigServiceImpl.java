package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.nove.http.HttpClientUtils;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.event.WatchEvent;
import global.redefine.watchdog.service.ApplicationConfigService;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by luqiang on 2018/5/31.
 */
@Service("applicationConfigService")
public class ApplicationConfigServiceImpl implements ApplicationConfigService {

    private static Logger LOG = LoggerFactory.getLogger(ApplicationConfigServiceImpl.class);
    @Autowired
    ApplicationContext applicationContext;

    /**
     * 获取应用数据列表
     */
    @Override
    public Map<String, Object> getApps() {


        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String defaultZone = config.get("redefine.eureka.defaultZone");

        Map<String, Object> apps = new LinkedHashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        try {

            String result = HttpClientUtils.doGet(defaultZone + "/apps", new HashMap<>(), "UTF-8", headers);
            Map<String, JSONObject> maps = JSON.parseObject(result, Map.class);
            JSONObject jsonObject = maps.get("applications");
            JSONArray jsonArray = (JSONArray) jsonObject.get("application");
            jsonArray.forEach(obj -> {

                List<Object> ips = new LinkedList<>();
                JSONObject o = (JSONObject) obj;
                String name = (String) o.get("name");
                JSONArray instances = (JSONArray) o.get("instance");
                instances.forEach(ins -> {
                    JSONObject instance = (JSONObject) ins;
                    String addr = (String) instance.get("ipAddr");
                    Map<String, String> data = (Map<String, String>) instance.get("metadata");
                    ips.add(addr + ":" + data.get("management.port"));
                });
                apps.put(name, ips);
            });
            return apps;
        } catch (Exception e) {
            LOG.error("获取应用数据列表ERROR:{}", e);
        }

        return apps;
    }

    /**
     * 获取服务配置
     */
    @Override
    public String getAppConfigs(String instanceId) {

        String url = "http://" + instanceId + "/env";
        try {
            return HttpClientUtils.doGet(url);
        } catch (Exception e) {
            LOG.error("获取服务配置ERROR:{}", e);
        }
        return "";
    }

    /**
     * DB 监控列表
     */
    @Override
    public Map<String, Object> getDBList() {

        Map<String, Object> apps = getApps();
        Map<String, Object> dbs = new LinkedHashMap<>();

        String dbmonitor = RedefineClusterUtils.getString("watchdog:dbmonitor");
        if (StringUtils.isNotEmpty(dbmonitor)) {
            return JSON.parseObject(dbmonitor, Map.class);
        }

        for (Map.Entry<String, Object> entry : apps.entrySet()) {
            List<String> nips = new LinkedList<>();
            List<String> instances = (List<String>) entry.getValue();
            for (String ins : instances) {
                String urlPre = "http://" + ins;
                nips.add(urlPre + "/druid");

            }
            if (nips.size() != 0) {
                dbs.put(entry.getKey(), nips);
            }
        }

        if (dbs.size() > 0) {
            RedefineClusterUtils.saveString("watchdog:dbmonitor", JSON.toJSONString(dbs), 1, TimeUnit.DAYS);
        }

        return dbs;
    }

    /**
     * 获取应用监控地址数据信息
     */
    @Override
    public String getMonitorHosts(String name) {


        Map<String, String> config = GitUtils.CONFIG_DATA.get();
        String host = config.get(name);

        return host;
    }

    /**
     * 刷新服务配置
     * destination 参数在这里用于限制传播 只刷新自己
     *
     * @return
     */
    @Override
    public boolean refreshConfig(String name, String ip) {

        boolean result = true;

        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        String[] hosts = ip.split(",");
        for (String host : hosts) {
            if (!result) {
                continue;
            }
            String req = "http://" + host + "/bus/refresh?destination=XXXX:YYYY";

            LOG.info("Strating refresh config request:{}", req);

            try {
                HttpClientUtils.setHttpParam(5000, 10000, 20000);
                String rt = HttpClientUtils.doPost(req);
                LOG.info("end refresh config request:{},response:{}", req, rt);
                //check
                Thread.sleep(5000);
                String check = HttpClientUtils.doGet("http://" + host + "/info");
                if (StringUtils.isNotEmpty(check) && check.contains("error")) {
                    result = false;
                    LOG.error("refresh config check fail,request:{}  refresh config stop", req);
                }

                Map<String, String> record = new HashMap<>();
                record.put("serviceName", name);
                record.put("user", host);
                StringBuilder builder = new StringBuilder();
                if (!result) {
                    record.put("type", "Refresh Config Fail");

                    builder.append("Service [ ").append(name).append(" ] ").append("IP: [ ").append(host).append(" ] Refresh Config Fail Please Check Service!!! ").append(" REQ: ").append(req);
                    record.put("details", builder.toString());

                } else {
                    record.put("type", "Refresh Config Succ");
                    builder.append("Service [ ").append(name).append(" ] ").append("IP: [ ").append(host).append(" ] Refresh Config Succ  ").append(" REQ: ").append(req);
                    record.put("details", builder.toString());

                }

                applicationContext.publishEvent(new WatchEvent(this, record));
            } catch (Exception e) {
                LOG.error("refresh config,request:{} ,error:{} refresh config stop", req, e.getMessage());
                result = false;
            }
        }

        System.out.println("1");
        return result;
    }

}
