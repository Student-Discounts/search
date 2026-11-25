package org.studentdiscounts.search.kafka.event.payload;

import java.util.List;
import java.util.Optional;

public record CatalogChangedPayload(
        Optional<PlaceSnapshot> place,
        List<OfferSnapshot> offers
) {
    public CatalogChangedPayload {
        if (offers == null) {
            offers = List.of();
        }
    }
}
