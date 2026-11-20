package org.studentdiscounts.search.kafka.event;

import org.studentdiscounts.search.kafka.event.payload.CatalogChangedPayload;

import java.time.Instant;
import java.util.UUID;

public record CatalogChangedEvent(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        UUID placeId,
        CatalogChangedPayload data
) {
}
