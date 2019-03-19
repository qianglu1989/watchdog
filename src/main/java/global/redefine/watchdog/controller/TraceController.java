package global.redefine.watchdog.controller;

import global.redefine.watchdog.service.TraceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by luqiang on 2018/5/28.
 *
 * @author luqiang
 */
//@RestController
//@RequestMapping("/trace")
//@CrossOrigin
public class TraceController {

    @Resource
    private TraceService traceService;


    /**
     * 根据traceid 获取数据
     * @return
     */
    @RequestMapping(value = "/getTraceDataById", method = RequestMethod.GET)
    public Map<String, String> getTraceDataById(@RequestParam String traceId,@RequestParam String date, HttpServletRequest request) {
//        GitUtils.initConfig(request);

        return traceService.getTraceDataById(traceId,date);
    }
    /**
     * 根据traceId spanid ﻿parentId获取数据详情
     *
     * @return
     */
    @RequestMapping(value = "/getTraceDetail", method = RequestMethod.GET)
    public Map<String, Object> getTraceDetail(@RequestParam String traceId,@RequestParam String spanId,@RequestParam(defaultValue = "") String parentId,@RequestParam String serviceName) {
        return traceService.getTraceDetail(traceId,spanId,parentId,serviceName);
    }
}
