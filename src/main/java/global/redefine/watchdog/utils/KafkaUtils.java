package global.redefine.watchdog.utils;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Properties;

public class KafkaUtils {


    public static AdminClient getClient(String server) {

        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, server);

        return AdminClient.create(props);
    }

    public static KafkaConsumer<String, String> getConsumeClient(String groupID, String bootstrapServer) {
        Properties props = new Properties();
        props.put("group.id", groupID);
        props.put("bootstrap.servers", bootstrapServer);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

        return consumer;
    }


}
