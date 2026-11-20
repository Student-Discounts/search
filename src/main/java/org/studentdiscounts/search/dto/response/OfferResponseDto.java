package org.studentdiscounts.search.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OfferResponseDto(
        UUID offerId,
        String discountType,
        BigDecimal discountValue,
        OffsetDateTime lastVerifiedAt,
        LocalDate validFrom,
        LocalDate validUntil,
        List<ScheduleGroupDto> schedule
) {
}
