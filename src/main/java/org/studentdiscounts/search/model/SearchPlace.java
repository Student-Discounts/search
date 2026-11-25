package org.studentdiscounts.search.model;

import java.util.UUID;

public record SearchPlace(
        UUID placeId,
        String name,
        String city,
        String addressLine,
        Location location,
        String categoryCode,
        String categoryName,
        String timezone
) {
}
