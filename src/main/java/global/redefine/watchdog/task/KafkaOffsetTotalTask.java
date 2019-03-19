package global.redefine.watchdog.task;


import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.utils.KafkaUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于统计每日topic流量
 */
@Component
public class KafkaOffsetTotalTask {

    private Logger LOG = LoggerFactory.getLogger(KafkaOffsetTotalTask.class);

    /**
     * 时间区域内检测kafka offset 延迟
     */
    @Scheduled(cron = "0 25 5 * * ?")
    public void redishartData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        String day = indDate.format(formatter);

        Map<Integer, Long> endOffsetMap = new HashMap<>();
        Map<Object, Object> hosts = RedefineClusterUtils.hgetAll("wathcdog:kafkacheck");

        hosts.forEach((name, host) -> {


            try {
                KafkaConsumer<String, String> consumer = KafkaUtils.getConsumeClient("wathcdog", String.valueOf(host));

                Map<String, List<PartitionInfo>> tps = consumer.listTopics();

                for (Map.Entry<String, List<PartitionInfo>> entry : tps.entrySet()) {

                    String topic = entry.getKey();
                    List<TopicPartition> topicPartitions = new ArrayList<>();

                    List<PartitionInfo> partitionsFor = entry.getValue();
                    for (PartitionInfo partitionInfo : partitionsFor) {
                        TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
                        topicPartitions.add(topicPartition);
                    }

                    //查询log size
                    Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
                    for (TopicPartition partitionInfo : endOffsets.keySet()) {
                        endOffsetMap.put(partitionInfo.partition(), endOffsets.get(partitionInfo));
                    }


                    //累加offSum
                    long offSum = 0L;
                    long totalSum = 0L;
                    for (Long off : endOffsets.values()) {
                        offSum += off;
                    }
                    ZonedDateTime time = indDate.minusDays(1);
                    //数据类型 totalSum_daysum
                    Object total = RedefineClusterUtils.hashGet("watchdog:kafkaflow:" + name + ":" + topic, time.format(formatter));
                    if (total != null) {
                        String[] data = String.valueOf(total).split("_");
                        totalSum = Long.valueOf(data[0]);
                    }
                    long daySum = offSum - totalSum;
                    RedefineClusterUtils.hashSet("watchdog:kafkaflow:" + name + ":" + topic, day, offSum + "_" + daySum);
                    LOG.info("cluster:{},topic:{},totalSum:{}.daySum:{}", name, topic, totalSum, daySum);
                }
            } catch (Exception e) {

            }

        });

    }
}
