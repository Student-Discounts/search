package org.studentdiscounts.search.service;

import org.studentdiscounts.search.kafka.event.CatalogChangedEvent;

public interface CatalogSyncService {

    void apply(CatalogChangedEvent catalogChangedEvent);
}
