package org.studentdiscounts.search.kafka.event.payload;

import java.util.List;

public record CatalogChangedPayload(
        PlaceSnapshot place,
        List<OfferSnapshot> offers
) {
    public CatalogChangedPayload {
        if (offers == null) {
            offers = List.of();
        }
    }
}
