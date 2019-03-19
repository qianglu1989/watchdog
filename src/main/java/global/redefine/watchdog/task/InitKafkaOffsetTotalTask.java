package global.redefine.watchdog.task;


import com.redefine.redis.utils.RedefineClusterUtils;
import global.redefine.watchdog.utils.KafkaUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 用于初始化测试数据topic流量
 */
//@Component
public class InitKafkaOffsetTotalTask {

    private Logger LOG = LoggerFactory.getLogger(InitKafkaOffsetTotalTask.class);
    @Resource
    private KafkaTemplate kafkaTemplate;

    /**
     * 时间区域内检测kafka offset 延迟
     */
    @Scheduled(fixedDelay = 10000)
    public void redishartData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime indDate = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        Map<Object, Object> hosts = RedefineClusterUtils.hgetAll("wathcdog:kafkacheck");

        hosts.forEach((name, host) -> {


            try {
                KafkaConsumer<String, String> consumer = KafkaUtils.getConsumeClient("wathcdog", String.valueOf(host));

                Map<String, List<PartitionInfo>> tps = consumer.listTopics();

                for (Map.Entry<String, List<PartitionInfo>> entry : tps.entrySet()) {

                    String topic = entry.getKey();
                    for (int i = 1; i < 14; i++) {
                        ZonedDateTime time = indDate.minusDays(i);
                        String d = time.format(formatter);
                        RedefineClusterUtils.hashSet("watchdog:kafkaflow:" + name + ":" + topic, d, RandomUtils.nextLong(0, 1000) + "_" + RandomUtils.nextLong(0, 100));

                    }
                }
            } catch (Exception e) {

            }

        });

    }
}
