package global.redefine.watchdog.controller;

import com.alibaba.fastjson.JSON;
import global.redefine.watchdog.service.ConfigService;
import global.redefine.watchdog.service.FeignConsumer;
import global.redefine.watchdog.utils.GitUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 服务配置
 * Created by luqiang on 2018/5/14.
 */
@RestController
@RequestMapping("/config")
@CrossOrigin
public class ServiceConfigController {

    Logger LOG = LoggerFactory.getLogger(ServiceConfigController.class);

    @Resource
    private ConfigService configService;


    @Resource
    private FeignConsumer feignConsumer;

    /**
     * 服务配置名称
     *
     * @return
     */
    @RequestMapping(value = "/getServices")
    public List<String> getServices(HttpServletRequest request, @RequestParam(defaultValue = "false", required = false) boolean type) {

        GitUtils.initConfigData(request);
        return configService.getServices(type);
    }

    /**
     * 服务配置详情
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/getServiceConfig")
    public Map<String, String> getServiceConfig(@RequestParam String name, HttpServletRequest request) {

        GitUtils.initConfigData(request);

        return configService.getServiceConfig(name);
    }

    /**
     * 用于AB TEST 专用
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getABConfigs")
    public Map<String, String> getABConfigs(HttpServletRequest request) {

        GitUtils.initConfigData(request);

        String profile = request.getHeader("profile");

        return configService.getServiceConfig("application-" + profile + ".properties");
    }

    /**
     * 用于AB TEST 专用
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/updateABConfigs")
    public boolean updateABConfigs(@RequestParam String data, HttpServletRequest request) {


        GitUtils.initConfigData(request);
        String profile = request.getHeader("profile");

        String fileName = "application-" + profile + ".properties";
        Map<String, String> before = configService.getServiceConfig(fileName);
        Map<String, String> after = JSON.parseObject(data, Map.class);
        boolean result = configService.updateConfig(fileName, after, "operation platform");

        if (result) {

            configService.refreshConfig();

            //send msg to data platform
            new Thread(() -> {
                diaposeData(before, after);
            }).start();
        }
        return result;
    }

    private void diaposeData(Map<String, String> before, Map<String, String> after) {

        List<Map<String, Object>> to = new LinkedList<>();

        for (String key : before.keySet()) {

            try {
                if (!after.containsKey(key)) {
                    String removeData = before.get(key);
                    Map<String, Object> data = JSON.parseObject(removeData, Map.class);
                    data.put("delete", true);
                    data.put("key", key);
                    to.add(data);
                } else {
                    String updata = equal(before.get(key), after.get(key));
                    if (StringUtils.isNotEmpty(updata)) {
                        Map<String, Object> data = JSON.parseObject(updata, Map.class);
                        data.put("delete", false);
                        data.put("key", key);
                        to.add(data);
                    }
                }
            } catch (Exception e) {
                LOG.error(" Nove ABTEST send msg to data platform before.keySet() error:{}", e);

            }

        }
        for (String key : after.keySet()) {

            try {
                if (!before.containsKey(key)) {
                    Map<String, Object> data = JSON.parseObject(after.get(key), Map.class);
                    data.put("delete", false);
                    data.put("key", key);
                    to.add(data);
                }
            } catch (Exception e) {
                LOG.error(" Nove ABTEST send msg to data platform after.keySet() error:{}", e);

            }

        }

//        //TODO SEND
//        StringEntity entity = new StringEntity(JSON.toJSONString(to), ContentType.APPLICATION_JSON);
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        String result = HttpClientUtils.doPost("http://172.23.4.195:8721/abtest/update", entity, headers);

        try {
            feignConsumer.updateAbtestInfo(to);

        } catch (Exception e) {
            LOG.error(" Nove ABTEST send msg to data platform error:{}", e);
        }
    }

    private String equal(String one, String two) {
        if (StringUtils.isEmpty(one) && StringUtils.isEmpty(two)) {
            return "";
        }

        Map<String, Object> oned = JSON.parseObject(one, Map.class);
        Map<String, Object> twod = JSON.parseObject(two, Map.class);

        if (!oned.get("date").equals(twod.get("date"))) {
            return two;
        }
        return "";
    }


    /**
     * 更新服务配置
     *
     * @return
     */
    @RequestMapping(value = "/updateConfig")
    public boolean updateConfig(@RequestBody Map data, HttpServletRequest request) {

        String token = GitUtils.getUserData(request);
        GitUtils.initConfigData(request);

        Map<String, Object> datas = (Map<String, Object>) data.get("params");
        LOG.info("user is :{} 更新服务配置 :{} ,数据:{}", token, "", data);
        return configService.updateConfig(String.valueOf(datas.get("name")), (Map<String, String>) datas.get("data"), token);
    }


}
