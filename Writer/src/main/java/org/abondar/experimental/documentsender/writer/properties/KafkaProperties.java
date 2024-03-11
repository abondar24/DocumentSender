package org.abondar.experimental.documentsender.writer.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
public  class KafkaProperties {
    private String topicName;
    private String broker;

    public String getTopicName() {
        return topicName;
    }

    public String getBroker() {
        return broker;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }
}
