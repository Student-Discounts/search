package org.studentdiscounts.search.kafka.event.payload;

import org.studentdiscounts.search.model.Location;

public record PlaceSnapshot(
        int revision,
        String placeName,
        String city,
        String addressLine,
        Location location,
        String categoryCode,
        String categoryName,
        String timezone
) {
}
