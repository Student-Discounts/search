package org.studentdiscounts.search.model;

import java.util.List;
import java.util.UUID;

public record SearchPlace(
        UUID placeId,
        String name,
        String city,
        String addressLine,
        String categoryCode,
        String categoryName,
        List<SearchOffer> offers
) {
}
