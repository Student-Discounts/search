package org.studentdiscounts.search.kafka.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.studentdiscounts.search.kafka.event.CatalogChangedEvent;
import org.studentdiscounts.search.service.CatalogSyncService;


@Component
@RequiredArgsConstructor
public class CatalogEventListener {

    private final CatalogSyncService catalogSyncService;

    @KafkaListener(topics = "${search.kafka.catalog-topic}")
    public void onCatalogChanged(CatalogChangedEvent event){
        catalogSyncService.apply(event);
    }
}
