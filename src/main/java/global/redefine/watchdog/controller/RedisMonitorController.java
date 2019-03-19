package global.redefine.watchdog.controller;


import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.RedisMonitorService;
import global.redefine.watchdog.utils.GitUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/redis")
@CrossOrigin
public class RedisMonitorController {

    @Resource
    private RedisMonitorService redisMonitorService;

    /**
     * 获取环境配置
     *
     * @return
     */
    @RequestMapping(value = "/getClusterInfo", method = RequestMethod.GET)
    public Map<String, String> getClusterInfo(HttpServletRequest request, @RequestParam(defaultValue = "", required = false) String host) {

        GitUtils.initConfig(request);

        return redisMonitorService.getClusterInfo(host);
    }

    /**
     * 获取环境IPS
     *
     * @return
     */
    @RequestMapping(value = "/getHosts", method = RequestMethod.GET)
    public String[] getHosts(HttpServletRequest request) {

        GitUtils.initConfig(request);

        return redisMonitorService.getHosts();
    }

    /**
     * 获取redis memory chart data
     *
     * @return
     */
    @RequestMapping(value = "/getMemoryChartData", method = RequestMethod.GET)
    public ChartModel getMemoryChartData(HttpServletRequest request, @RequestParam(defaultValue = "", required = false) String host) {

        GitUtils.initConfig(request);

        return redisMonitorService.getMemoryChartData(host);
    }

    /**
     * 获取redis ops chart data
     *
     * @return
     */
    @RequestMapping(value = "/getOpsChartData", method = RequestMethod.GET)
    public ChartModel getOpsChartData(HttpServletRequest request, @RequestParam(defaultValue = "", required = false) String host) {

        GitUtils.initConfig(request);

        return redisMonitorService.getOpsChartData(host);
    }
}
