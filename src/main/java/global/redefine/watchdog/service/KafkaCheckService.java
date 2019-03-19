package global.redefine.watchdog.service;

import global.redefine.watchdog.po.ChartModel;

import java.util.List;
import java.util.Map;

public interface KafkaCheckService {

    /**
     * 添加集群信息
     *
     * @param bootStrap 添加一个ip即可
     * @return
     */
    public boolean addClustrInfo(String name, String bootStrap);


    /**
     * @param clusterName 集群代号
     * @return
     */
    public Map<String, Object> getKafkaClusterInfo(String clusterName);

    public boolean deleteCluster(String name);

    /**
     * 添加监控信息
     *
     * @param name    集群名称
     * @param groupId 监控组
     * @param topics  监控topic
     * @param limit   报警阈值
     * @return
     */
    public boolean addmonitor(String name, String groupId, String topics, String limit);

    /**
     * 查询阈值报警数据
     *
     * @return
     */
    public List<Map<Object, Object>> getMonitorDatas();

    /**
     * 删除阈值报警数据
     *
     * @return
     */
    public boolean delMonitor(String clusterName, String groupId);

    /**
     * 根据集群名称获取topic流量图
     *
     * @param clusterName
     * @param topic
     * @return
     */
    public ChartModel getChartByTopic(String clusterName, String topic);
}
