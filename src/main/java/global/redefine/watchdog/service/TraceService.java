package global.redefine.watchdog.service;

import java.util.Map;

/**
 * Created by luqiang on 2018/5/28.
 */
public interface TraceService {



    /**
     *根据traceId获取监控链数据
     *
     * @return
     */
    public Map<String,String> getTraceDataById(String traceId,String date);

    /**
     * 根据traceId spanid ﻿parentId获取数据详情
     *
     * @return
     */
    public Map<String, Object> getTraceDetail(String traceId,String spanId,String parentId,String serviceName);
}
