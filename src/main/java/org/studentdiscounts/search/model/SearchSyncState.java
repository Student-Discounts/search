package org.studentdiscounts.search.model;

import java.util.UUID;

public record SearchSyncState(
        EntityType entityType,
        UUID entityId,
        int revision,
        boolean deleted

) {
    public enum EntityType {
        PLACE,
        OFFER
    }
}
