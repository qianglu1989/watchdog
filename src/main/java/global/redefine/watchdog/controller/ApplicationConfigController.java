package global.redefine.watchdog.controller;

import global.redefine.watchdog.service.ApplicationConfigService;
import global.redefine.watchdog.utils.GitUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by luqiang on 2018/5/31.
 *
 * @author luqiang 应用配置监控
 */
@RequestMapping("/appConfig")
@RestController
@CrossOrigin
@RefreshScope
public class ApplicationConfigController {


    @Resource
    private ApplicationConfigService applicationConfigService;




    /**
     * 获取服务配置列表
     *
     * @return
     */
    @RequestMapping(value = "/getApps", method = RequestMethod.GET)
    public Map<String, Object> getApps(HttpServletRequest request) {

        GitUtils.initConfigData(request);

        return applicationConfigService.getApps();
    }


    /**
     * 获取服务配置
     *
     * @return
     */
    @RequestMapping(value = "/getAppConfigs", method = RequestMethod.GET)
    public String getAppConfigs(@RequestParam String instanceId, HttpServletRequest request) {
        GitUtils.initConfigData(request);

        return applicationConfigService.getAppConfigs(instanceId);
    }


    /**
     * 获取DB服务配置列表
     *
     * @return
     */
    @RequestMapping(value = "/getDBList", method = RequestMethod.GET)
    public Map<String, Object> getDBList(HttpServletRequest request) {
        GitUtils.initConfigData(request);

        return applicationConfigService.getDBList();
    }

    /**
     * 获取应用监控地址数据信息
     */
    @RequestMapping(value = "/getMonitorHosts",method = RequestMethod.GET)
    public String getMonitorHosts(HttpServletRequest request,@RequestParam String name){
        GitUtils.initConfigData(request);

        return applicationConfigService.getMonitorHosts(name);
    }

    /**
     * 刷新服务配置
     *
     * @return
     */
    @RequestMapping(value = "/refresh")
    public boolean refresh( @RequestParam String name, @RequestParam String ip) {


        return applicationConfigService.refreshConfig(name, ip);
    }

}
