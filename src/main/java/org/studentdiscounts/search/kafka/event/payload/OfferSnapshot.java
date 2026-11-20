package org.studentdiscounts.search.kafka.event.payload;

import org.studentdiscounts.search.model.DiscountType;
import org.studentdiscounts.search.model.ScheduleGroup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OfferSnapshot(
        UUID offerId,
        int revision,
        OfferStatus status,
        DiscountType discountType,
        BigDecimal discountValue,
        OffsetDateTime lastVerifiedAt,
        LocalDate validFrom,
        LocalDate validUntil,
        List<ScheduleGroup> schedule
) {
    public enum OfferStatus {
        ACTIVE,
        SUSPENDED,
        EXPIRED,
        ARCHIVED
    }
}
