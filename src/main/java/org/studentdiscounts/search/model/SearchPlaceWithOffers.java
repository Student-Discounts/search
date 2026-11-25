package org.studentdiscounts.search.model;

import java.util.List;

public record SearchPlaceWithOffers(
        SearchPlace place,
        List<SearchOffer> offers
) {
}
