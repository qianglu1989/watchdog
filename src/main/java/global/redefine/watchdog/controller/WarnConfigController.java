package global.redefine.watchdog.controller;

import global.redefine.watchdog.service.WarnConfigService;
import global.redefine.watchdog.utils.GitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 服务配置
 * Created by luqiang on 2018/5/14.
 */
@RestController
@RequestMapping("/warn")
@CrossOrigin
public class WarnConfigController {
    Logger LOG = LoggerFactory.getLogger(WarnConfigController.class);

    @Resource
    private WarnConfigService warnConfigService;

    /**
     * 服务配置名称
     *
     * @return
     */
    @RequestMapping(value = "/getWarnServices")
    public List<String> getWarnServices(HttpServletRequest request) {
        GitUtils.initConfigData(request);

        return warnConfigService.getWarnServices();
    }

    /**
     * 服务配置更新
     *
     * @param data
     * @return
     */
    @RequestMapping(value = "/updateWarnConfig")
    public boolean updateWarnConfig(@RequestParam String data, HttpServletRequest request) {

        LOG.info("token is :{} 更新服务报警配置 ,数据:{}", GitUtils.getToken(request), data);
        String token = GitUtils.getUserData(request);
        GitUtils.initConfigData(request);

        return warnConfigService.updateWarnConfig(data, token);
    }

    /**
     * 查询服务配置
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/getWarnConfigs")
    public Map<String, String> getWarnConfigs(
            @RequestParam String name, HttpServletRequest request) {
        GitUtils.initConfigData(request);

        return warnConfigService.getWarnConfigs(name);
    }
//
}
