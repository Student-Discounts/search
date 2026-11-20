package org.studentdiscounts.search.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SearchOffer(
        UUID offerId,
        UUID placeId,
        DiscountType discountType,
        BigDecimal discountValue,
        OffsetDateTime lastVerifiedAt,
        LocalDate validFrom,
        LocalDate validUntil,
        List<ScheduleGroup> schedule

) {
}
