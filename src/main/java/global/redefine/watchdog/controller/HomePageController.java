package global.redefine.watchdog.controller;

import com.netflix.discovery.DiscoveryManager;
import com.netflix.discovery.shared.Applications;
import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.HomeService;
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
@RequestMapping("/home")
@CrossOrigin
public class HomePageController {
    Logger LOG = LoggerFactory.getLogger(HomePageController.class);


    @Resource
    private HomeService homeService;


    /**
     * 服务配置名称
     *
     * @return
     */
    @RequestMapping(value = "/getapps", method = RequestMethod.GET)
    public Applications getapps() {

       return  DiscoveryManager.getInstance().getEurekaClient().getApplications();
    }

    /**
     * 获取首页基础数据
     *
     * @return
     */
    @RequestMapping(value = "/getHomeData", method = RequestMethod.GET)
    public Map<String, Object> getHomeData(HttpServletRequest request) {
        GitUtils.initConfig(request);


        return  homeService.getHomeData();
    }

    /**
     * 获取首页饼图
     *
     * @return
     */
    @RequestMapping(value = "/getPipData", method = RequestMethod.GET)
    public List<Map<Object, Object>> getPipData(HttpServletRequest request) {
        GitUtils.initConfig(request);
        return  homeService.getPipData();
    }

    /**
     * 获取首页饼图
     *
     * @return
     */
    @RequestMapping(value = "/methodDuration", method = RequestMethod.GET)
    public List<Map<Object, Object>> methodDuration(HttpServletRequest request) {
        GitUtils.initConfig(request);
        return  homeService.methodDuration();
    }


    @RequestMapping(value = "/concurrent", method = RequestMethod.GET)
    public List<Map<Object, Object>> concurrent(HttpServletRequest request) {
        GitUtils.initConfig(request);
        return  homeService.concurrent();
    }


    /**
     * 获取图表数据
     *
     * @return
     */
    @RequestMapping(value = "/getHomeChartData")
    public List<ChartModel> getHomeChartData(HttpServletRequest request, @RequestParam(defaultValue = "0", required = false) String type, @RequestParam(defaultValue = "", required = false) String date) {

        GitUtils.initConfig(request);

        return homeService.getHomeChartData(type,date);
    }
    /**
     * 获取图表数据
     *
     * @return
     */
    @RequestMapping(value = "/getDurationChartData")
    public List<ChartModel> getDurationChartData(HttpServletRequest request, @RequestParam(defaultValue = "0", required = false) String type, @RequestParam(defaultValue = "", required = false) String date) {

        GitUtils.initConfig(request);

        return homeService.getDurationChartData(type,date);
    }

}
