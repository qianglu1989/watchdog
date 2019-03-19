package global.redefine.watchdog.po;

import java.io.Serializable;

/**
 * 用于kafka数据监控
 */
public class KafkaItem implements Serializable {

    private String topic;

    private String partitions;

    private String brokers;

    private String replicas;


    private int partCount;

    private int brokerCount;

    private int replicaCount;

    private long offset;

    private long offsetLag;



    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPartitions() {
        return partitions;
    }

    public void setPartitions(String partitions) {
        this.partitions = partitions;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public String getReplicas() {
        return replicas;
    }

    public void setReplicas(String replicas) {
        this.replicas = replicas;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getOffsetLag() {
        return offsetLag;
    }

    public void setOffsetLag(long offsetLag) {
        this.offsetLag = offsetLag;
    }

    public int getPartCount() {
        return partCount;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public int getBrokerCount() {
        return brokerCount;
    }

    public void setBrokerCount(int brokerCount) {
        this.brokerCount = brokerCount;
    }

    public int getReplicaCount() {
        return replicaCount;
    }

    public void setReplicaCount(int replicaCount) {
        this.replicaCount = replicaCount;
    }

    public static KafkaBuilder builder() {
        return new KafkaBuilder();
    }

    public static class KafkaBuilder {

        private KafkaItem kafkaItem = new KafkaItem();

        public KafkaBuilder topic(String topic) {
            this.kafkaItem.setTopic(topic);
            return this;
        }

        public KafkaBuilder partitions(String partitions) {
            this.kafkaItem.setPartitions(partitions);
            return this;
        }

        public KafkaBuilder brokers(String brokers) {
            this.kafkaItem.setBrokers(brokers);
            return this;
        }

        public KafkaBuilder replicas(String replicas) {
            this.kafkaItem.setReplicas(replicas);
            return this;
        }

        public KafkaBuilder offset(long offset) {
            this.kafkaItem.setOffset(offset);
            return this;
        }

        public KafkaBuilder offsetLag(long offsetLag) {
            this.kafkaItem.setOffsetLag(offsetLag);
            return this;
        }


        public KafkaBuilder partCount(int partCount) {
            this.kafkaItem.setPartCount(partCount);
            return this;
        }

        public KafkaBuilder brokerCount(int brokerCount) {
            this.kafkaItem.setBrokerCount(brokerCount);
            return this;
        }

        public KafkaBuilder replicaCount(int replicaCount) {
            this.kafkaItem.setReplicaCount(replicaCount);
            return this;
        }

        public KafkaItem build() {
            return kafkaItem;
        }
    }
}
