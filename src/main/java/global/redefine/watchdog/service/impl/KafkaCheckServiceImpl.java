package global.redefine.watchdog.service.impl;

import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.po.ChartModel;
import global.redefine.watchdog.po.KafkaItem;
import global.redefine.watchdog.service.KafkaCheckService;
import global.redefine.watchdog.utils.KafkaUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * kafka monitor
 */
@Service("kafkaCheckService")
public class KafkaCheckServiceImpl implements KafkaCheckService {

    private static Logger LOG = LoggerFactory.getLogger(KafkaCheckServiceImpl.class);

    /**
     * 添加集群信息
     *
     * @param bootStrap 添加一个ip即可
     * @return
     */
    @Override
    public boolean addClustrInfo(String name, String bootStrap) {

        RedefineClusterUtils.hashSet("wathcdog:kafkacheck", name, bootStrap);
        return true;
    }


    @Override
    public boolean deleteCluster(String name) {

        RedefineClusterUtils.hDel("wathcdog:kafkacheck", name);
        return true;
    }

    /**
     * @param clusterName 集群代号
     * @return
     */
    @Override
    public Map<String, Object> getKafkaClusterInfo(String clusterName) {

        Map<String, Object> result = new HashMap<>();
        List<KafkaItem> items = new LinkedList<>();
        KafkaConsumer<String, String> consumer = getAdminClient(clusterName);

        try {
            Map<String, List<PartitionInfo>> topics = consumer.listTopics();
            Set<String> nodes = new HashSet<>();
            topics.forEach((k, v) -> {
                Set<String> hosts = new HashSet<>();
                List<TopicPartition> topicPartitions = new ArrayList<TopicPartition>();

                KafkaItem.KafkaBuilder builder = new KafkaItem.KafkaBuilder();
                builder.topic(k);
                v.forEach(partitionInfo -> {
                            hosts.add(partitionInfo.leader().host());
                            nodes.add(partitionInfo.leader().host());
                            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                            topicPartitions.add(topicPartition);
                        }
                );

                builder.partCount(v.size()).replicaCount(v.get(0).replicas().length).brokerCount(hosts.size()).brokers(JSON.toJSONString(hosts));
                Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
                long offset = 0;
                for (Long endOffset : endOffsets.values()) {
                    offset += endOffset;
                }
                KafkaItem item = builder.offset(offset).build();
                items.add(item);
            });
            result.put("topics", items);
            result.put("nodes", nodes);
        } catch (Exception e) {
        } finally {
            consumer.close();
        }

        return result;
    }


    private KafkaConsumer<String, String> getAdminClient(String name) {

        Object clusterInfo = RedefineClusterUtils.hashGet("wathcdog:kafkacheck", name);
        KafkaConsumer<String, String> client = KafkaUtils.getConsumeClient("test", String.valueOf(clusterInfo));

        return client;
    }

    /**
     * 添加监控信息
     *
     * @param name    集群名称
     * @param groupId 监控组
     * @param topics  监控topic
     * @param limit   报警阈值
     * @return
     */
    @Override
    public boolean addmonitor(String name, String groupId, String topics, String limit) {


        if (StringUtils.isEmpty(name)) {
            return false;
        }
        Map<String, Object> monitor = new HashMap<>();
        monitor.put("topics", topics);
        monitor.put("limit", limit);

        RedefineClusterUtils.hashSet("watchdog:kafkamonitor:" + name, groupId, JSON.toJSONString(monitor));

        return true;
    }

    /**
     * 查询阈值报警数据
     *
     * @return
     */
    @Override
    public List<Map<Object, Object>> getMonitorDatas() {

        List<Map<Object, Object>> result = new LinkedList<>();
        Map<Object, Object> hosts = RedefineClusterUtils.hgetAll("wathcdog:kafkacheck");

        hosts.forEach((name, host) -> {
            Map<Object, Object> data = RedefineClusterUtils.hgetAll("watchdog:kafkamonitor:" + String.valueOf(name));

            if (MapUtils.isEmpty(data)) {
                return;
            }

            data.forEach((groupId, datas) -> {
                Map<Object, Object> mon = new HashMap<>();

                mon.put("clusterName", name);
                mon.put("groupId", groupId);
                Map<Object, Object> topicdata = JSON.parseObject(String.valueOf(datas), Map.class);
                mon.putAll(topicdata);
                result.add(mon);

            });

        });
        return result;
    }

    /**
     * 删除阈值报警数据
     *
     * @return
     */
    @Override
    public boolean delMonitor(String clusterName, String groupId) {

        if (StringUtils.isEmpty(clusterName)) {
            return false;
        }
        RedefineClusterUtils.hDel("watchdog:kafkamonitor:" + clusterName, groupId);
        return true;
    }

    /**
     * 根据集群名称获取topic流量图
     *
     * @param clusterName
     * @param topic
     * @return
     */
    @Override
    public ChartModel getChartByTopic(String clusterName, String topic) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        LinkedList<Object> x = new LinkedList<>();
        LinkedList<Object> y = new LinkedList<>();
        ChartModel model = new ChartModel();
        for (int i = 1; i < 14; i++) {
            ZonedDateTime time = indDate.minusDays(i);
            String day = time.format(formatter);

            Object data = RedefineClusterUtils.hashGet("watchdog:kafkaflow:" + clusterName + ":" + topic, day);

            if (data == null) {
                x.add(0);
            } else {
                String[] td = String.valueOf(data).split("_");
                x.add(td[1]);
            }
            y.add(day);


        }
        Collections.reverse(x);
        Collections.reverse(y);
        model.setName(topic);
        model.setXaxis(x);
        model.setYaxis(y);


        return model;
    }
}
