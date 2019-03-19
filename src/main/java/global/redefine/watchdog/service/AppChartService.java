package global.redefine.watchdog.service;

import global.redefine.watchdog.po.ChartModel;

import java.util.List;
import java.util.Set;
@Deprecated
public interface AppChartService {

    /**
     * 获取图表基础参数
     *
     * @param serviceName 如果不传，并且t
     * @param type        为0 ，就查询应用列表，1 查询clazz 信息，2 查询method 信息
     * @param clazz       类名称只有在查询方法名称时候需要
     * @return
     */
    public Set<String> getBaseData(String serviceName, String clazz);

    /**
     * 添加数据图表
     *
     * @param chartName ultron:testController:test
     * @return
     */
    public boolean addChartData(String chartName);


    /**
     * 查询图表数据
     *
     * @param chartName
     * @param type      代表获取图表数据的时间区间，0 一小时内，1 24小时，2一周
     * @return
     */
    public List<ChartModel> getChartData(String chartName, String type, String date);


    /**
     * 删除数据图表
     *
     * @param chartName ultron:testController:test
     * @return
     */
    public boolean deleteChartData(String chartName);
}
