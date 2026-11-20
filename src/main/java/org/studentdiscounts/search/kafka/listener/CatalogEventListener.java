package org.studentdiscounts.search.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.studentdiscounts.search.kafka.event.CatalogChangedEvent;

@Slf4j
@Component
public class CatalogEventListener {

    @KafkaListener(topics = "${search.kafka.catalog-topic}")
    public void onCatalogChanged(CatalogChangedEvent event){
        log.info(
                "Received CatalogChanged eventId={}, placeId={}, hasPlace={}, offerCount={}",
                event.eventId(),
                event.placeId(),
                event.data().place() != null,
                event.data().offers().size()
        );
    }
}
