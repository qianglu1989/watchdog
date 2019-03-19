package global.redefine.watchdog.controller;

import global.redefine.watchdog.service.AppManagerService;
import global.redefine.watchdog.utils.GitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 应用管理中心 负责管理线上应用集群，比如权限配置
 * Created by luqiang on 2018/5/14.
 * @author QIANG
 */
@RestController
@RequestMapping("/manager")
@CrossOrigin
public class AppManagerController {

    Logger LOG = LoggerFactory.getLogger(AppManagerController.class);

    @Resource
    private AppManagerService appManagerService;

    /**
     * 服务配置名称
     *
     * @return
     */
    @RequestMapping(value = "/getMetaData",method = RequestMethod.GET)
    public Map<String, Object> getMetaData(HttpServletRequest request) {

        GitUtils.initConfigData(request);

        return appManagerService.getMetaData();
    }


    /**
     * 更新服务配置
     *
     * @return
     */
    @RequestMapping(value = "/updateMetaData",method = RequestMethod.GET)
    public boolean updateMetaData(HttpServletRequest request,@RequestParam String app,@RequestParam String instanceId,@RequestParam String weight ) {

        GitUtils.initConfigData(request);
        return appManagerService.updateMetaData(app,instanceId,weight);
    }
//
//    /**
//     * 刷新服务配置
//     *
//     * @return
//     */
//    @RequestMapping(value = "/refresh")
//    public boolean refresh(HttpServletRequest request) {
//
//        LOG.info("token is :{} 刷新服务配置 ", GitUtils.getToken(request));
//
//        return configService.refreshConfig();
//    }


}
