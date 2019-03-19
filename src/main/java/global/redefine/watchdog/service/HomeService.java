package global.redefine.watchdog.service;

import global.redefine.watchdog.po.ChartModel;

import java.util.List;
import java.util.Map;

public interface HomeService {


    /**
     * 获取首页基础数据
     * @return
     */
    public Map<String, Object> getHomeData();

    /**
     * 获取首页饼图
     * @return
     */
    public List<Map<Object,Object>> getPipData();

    /**
     * 获取方法耗时排行
     * @return
     */
    public List<Map<Object,Object >> methodDuration();

    /**
     * 首页图表数据
     *
     * @return
     */
    public List<ChartModel> getHomeChartData(String type, String date);

    /**
     * 获取并发
     * @return
     */
    public List<Map<Object, Object>> concurrent();


    /**
     * 获取首页响应时长数据图表
     *
     * @param type
     * @param date
     * @return
     */
    public List<ChartModel> getDurationChartData(String type, String date);
}
