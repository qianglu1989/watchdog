package global.redefine.watchdog.service;

import global.redefine.watchdog.po.ChartModel;

import java.util.Map;

public interface RedisMonitorService {

    /**
     * 获取集群基础信息
     * @return
     */
    public Map<String, String> getClusterInfo(String host);

    /**
     * 获取集群hosts
     *
     * @return
     */
    public String[] getHosts();

    /**
     * 统计redis图表数据
     */
    public void chartData();

    /**
     * 获取redis memory监控图表数据
     * @param host
     * @return
     */
    public ChartModel getMemoryChartData(String host);

    /**
     * 获取redis ops
     * @param host
     * @return
     */
    public ChartModel getOpsChartData(String host);
}
