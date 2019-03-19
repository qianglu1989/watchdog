package global.redefine.watchdog.controller;

import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.service.DataService;
import global.redefine.watchdog.utils.GitUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 数据排行榜
 * Created by luqiang on 2018/5/18.
 */
@RestController
@RequestMapping("/data")
@CrossOrigin
public class DataController {

    @Resource
    private DataService dataService;

    @RequestMapping(value = "/errorList", method = RequestMethod.GET)
    public Map<Object, Object> errorList(HttpServletRequest request) {
        return dataService.errorList();
    }

    @RequestMapping(value = "/serviceList", method = RequestMethod.GET)
    public Map<Object, Object> serviceList(HttpServletRequest request) {
        GitUtils.initConfig(request);
        return dataService.serviceList();
    }

    @RequestMapping(value = "/interfaceTop", method = RequestMethod.GET)
    public Map<Object, Object> interfaceTop(HttpServletRequest request, @RequestParam(defaultValue = "", required = false) String name, @RequestParam(defaultValue = "", required = false) String date) {
        GitUtils.initConfig(request);
        return dataService.interfaceTop(name, date);
    }

    @RequestMapping(value = "/interfaceChart", method = RequestMethod.GET)
    public ChartModel interfaceChart(HttpServletRequest request, @RequestParam(defaultValue = "", required = false) String name, @RequestParam String face) {
        GitUtils.initConfig(request);
        return dataService.interfaceChart(name, face);
    }
}
