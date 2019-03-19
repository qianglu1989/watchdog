package global.redefine.watchdog.service;

import global.redefine.watchdog.po.ChartModel;

import java.util.Map;

public interface DataService {

    /**
     * 异常排行榜
     * @return
     */
    public Map<Object, Object> errorList();

    /**
     * 服务响应排行榜
     *
     * @return
     */
    public Map<Object, Object> serviceList();

    /**
     * 接口调用排行榜
     *
     * @return
     */
    public Map<Object, Object> interfaceTop(String name, String date);

    /**
     * 接口调用周图，根据应用显示本周接口图
     * 根据不同项目 不同时间段获取数据信息
     * 对数据进行缓存处理，默认失效5分
     *
     * @param name 应用
     * @return
     */
    public ChartModel interfaceChart(String name,String face);
}
