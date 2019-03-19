package global.redefine.watchdog.task;


import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.utils.KafkaUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaOffsetMonitorTask {


    @Resource
    private KafkaTemplate kafkaTemplate;

    /**
     * 时间区域内检测kafka offset 延迟
     */
    @Scheduled(fixedDelay = 300000)
    public void redishartData() {

        Map<Integer, Long> endOffsetMap = new HashMap<>();
        Map<Integer, Long> commitOffsetMap = new HashMap<>();
        Map<Object, Object> hosts = RedefineClusterUtils.hgetAll("wathcdog:kafkacheck");

        hosts.forEach((name, host) -> {
            Map<Object, Object> check = RedefineClusterUtils.hgetAll("watchdog:kafkamonitor:" + String.valueOf(name));

            if (MapUtils.isEmpty(check)) {
                return;
            }

            check.forEach((groupId, datas) -> {
                try {
                    KafkaConsumer<String, String> consumer = KafkaUtils.getConsumeClient(String.valueOf(groupId), String.valueOf(host));

                    Map<String, Object> td = JSON.parseObject(String.valueOf(datas), Map.class);
                    String[] tps = String.valueOf(td.get("topics")).split(",");

                    for (String topic : tps) {
                        List<TopicPartition> topicPartitions = new ArrayList<TopicPartition>();

                        List<PartitionInfo> partitionsFor = consumer.partitionsFor(topic);
                        for (PartitionInfo partitionInfo : partitionsFor) {
                            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                            topicPartitions.add(topicPartition);
                        }

                        //查询log size
                        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
                        for (TopicPartition partitionInfo : endOffsets.keySet()) {
                            endOffsetMap.put(partitionInfo.partition(), endOffsets.get(partitionInfo));
                        }

                        //查询消费offset
                        for (TopicPartition topicAndPartition : topicPartitions) {
                            OffsetAndMetadata committed = consumer.committed(topicAndPartition);
                            if (committed == null) {
                                continue;
                            }
                            commitOffsetMap.put(topicAndPartition.partition(), committed.offset());
                        }

                        //累加lag
                        long lagSum = 0L;
                        long offset = 0L;
                        for (Integer partition : endOffsetMap.keySet()) {
                            long endOffSet = endOffsetMap.get(partition);
                            long commitOffSet = commitOffsetMap.get(partition);
                            long diffOffset = endOffSet - commitOffSet;
                            lagSum += diffOffset;
                            offset += endOffSet;
                        }

                        long limit = Long.valueOf((String) td.get("limit"));
                        if (lagSum > (limit == 0L ? 5000 : limit)) {
                            Map<String, Object> result = new HashMap<>();
                            result.put("topic", topic);
                            result.put("offset", offset);
                            result.put("groupId", groupId);
                            result.put("lag", lagSum);
                            kafkaTemplate.send("kafkamonitor", JSON.toJSONString(result).getBytes());
                        }

                    }
                } catch (Exception e) {

                }
            });

        });

    }
}
