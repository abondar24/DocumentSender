package org.abondar.experimental.document.sender.writer.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "kafka")
@Getter
@Setter
public  class KafkaProperties {
    private String topicName;
    private String broker;
}
