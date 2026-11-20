package org.studentdiscounts.search.kafka.config;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializerConfig;
import org.springframework.boot.kafka.autoconfigure.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.studentdiscounts.search.kafka.event.CatalogChangedEvent;

import java.util.Map;

@Configuration
public class CatalogKafkaConfiguration {

    @Bean
    DefaultKafkaConsumerFactoryCustomizer catalogEventType() {
        return factory -> factory.updateConfigs(Map.of(
                KafkaJsonSchemaDeserializerConfig.JSON_VALUE_TYPE,
                CatalogChangedEvent.class
        ));
    }
}
