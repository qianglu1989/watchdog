package global.redefine.watchdog.controller;

import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.AppChartService;
import global.redefine.watchdog.utils.GitUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * 用于图表展示管理
 * Created by luqiang on 2018/5/14.
 *
 * @author QIANG
 */
@RestController
@RequestMapping("/chart")
@CrossOrigin
@Deprecated
public class AppChartController {

    Logger LOG = LoggerFactory.getLogger(AppChartController.class);

    @Resource
    private AppChartService appChartService;

    /**
     * 获取图表基础参数
     *
     * @return
     */
    @RequestMapping(value = "/getBaseData", method = RequestMethod.GET)
    public Set<String> getBaseData(HttpServletRequest request, @RequestParam(defaultValue = "", required = false) String serviceName, @RequestParam(defaultValue = "", required = false) String clazz) {

        GitUtils.initConfig(request);
        return appChartService.getBaseData(serviceName, clazz);
    }


    /**
     * 添加图表信息
     *
     * @return
     */
    @RequestMapping(value = "/addChartData", method = RequestMethod.GET)
    public boolean addChartData(HttpServletRequest request, @RequestParam String chartName) {
        GitUtils.initConfig(request);

        return appChartService.addChartData(chartName);
    }

    /**
     * 删除图表信息
     *
     * @return
     */
    @RequestMapping(value = "/deleteChartData", method = RequestMethod.GET)
    public boolean deleteChartData(HttpServletRequest request, @RequestParam String chartName) {
        GitUtils.initConfig(request);

        return appChartService.deleteChartData(chartName);
    }

    /**
     * 获取图表数据
     *
     * @return
     */
    @RequestMapping(value = "/getChartData")
    public List<ChartModel> getChartData(HttpServletRequest request, @RequestParam(defaultValue = "", required = false) String chartName, @RequestParam(defaultValue = "0", required = false) String type, @RequestParam(defaultValue = "", required = false) String date) {

        GitUtils.initConfig(request);

        return appChartService.getChartData(chartName, type,date);
    }




}
