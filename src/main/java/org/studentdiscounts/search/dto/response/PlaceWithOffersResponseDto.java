package org.studentdiscounts.search.dto.response;

import java.util.List;
import java.util.UUID;

public record PlaceWithOffersResponseDto(
        UUID placeId,
        String name,
        String city,
        String addressLine,
        String categoryCode,
        String categoryName,
        List<OfferResponseDto> offers
) {
}
