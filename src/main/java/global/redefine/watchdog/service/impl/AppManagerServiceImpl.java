package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redefine.nove.http.HttpClientUtils;
import global.redefine.watchdog.service.AppManagerService;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by luqiang on 2018/7/6.
 *
 * @author QIANG
 */
@Service("appManagerService")
public class AppManagerServiceImpl implements AppManagerService {

    private static Logger LOG = LoggerFactory.getLogger(AppManagerServiceImpl.class);


    /**
     * 获取应用mete数据列表
     */
    @Override
    public Map<String, Object> getMetaData() {


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

                JSONObject o = (JSONObject) obj;
                String name = (String) o.get("name");
                JSONArray instances = (JSONArray) o.get("instance");

                instances.forEach(ins -> {

                    Map<String, String> ipData = new HashMap<>();
                    StringBuilder builder = new StringBuilder(name);
                    JSONObject instance = (JSONObject) ins;
                    String instanceId = (String) instance.get("instanceId");
                    builder.append(" (").append(instanceId).append(")");
                    Map<String, String> data = (Map<String, String>) instance.get("metadata");
                    String weight = data.get("weight");
                    ipData.put("app", (String) instance.get("app"));
                    ipData.put("instanceId", instanceId);
                    ipData.put("weight", StringUtils.isEmpty(weight) ? "10" : weight);
                    ipData.put("ip", (String) instance.get("ipAddr"));
                    ipData.put("port", data.get("management.port"));
                    apps.put(builder.toString(), ipData);

                });
            });
            return apps;
        } catch (Exception e) {
            LOG.error("获取应用数据列表ERROR:{}", e);
        }

        return apps;
    }


    /**
     * 更新mete配置信息
     *
     * @param app
     * @param instanceId
     * @return
     */
    @Override
    public boolean updateMetaData(String app, String instanceId, String weight) {

        boolean result = true;

        try {


            Map<String, String> config = GitUtils.CONFIG_DATA.get();
            String defaultZone = config.get("redefine.eureka.defaultZone");

            StringBuilder builder = new StringBuilder();
            builder.append(defaultZone).append("apps/").append(app).append("/").append(instanceId).append("/metadata");

            Map<String, Object> params = new HashMap<>();
            params.put("weight", weight);

            String rd = HttpClientUtils.doPut(builder.toString(), params, "UTF-8", null);

            System.out.println(rd);
        } catch (Exception e) {
            System.out.println(e);
        }

        return result;
    }


}
