package global.redefine.watchdog.listener;

import com.alibaba.fastjson.JSON;
import global.redefine.watchdog.event.WatchEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author luqiang on 28/12/2018.
 */
public class WatchListener implements ApplicationListener<WatchEvent> {

    private KafkaTemplate kafkaTemplate;

    public WatchListener(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void onApplicationEvent(WatchEvent event) {
        kafkaTemplate.send("exwarnConfig", JSON.toJSONString(event.getMsg()).getBytes());

    }
}
