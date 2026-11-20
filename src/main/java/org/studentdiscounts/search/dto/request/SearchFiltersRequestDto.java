package org.studentdiscounts.search.dto.request;

import jakarta.validation.constraints.AssertTrue;
import org.studentdiscounts.search.model.DiscountType;

import java.util.Arrays;

public record SearchFiltersRequestDto(
        String q,
        String city,
        String categoryCode,
        String discountType
) {
    @AssertTrue(message = "Unknown discount type")
    public boolean isDiscountTypeValid() {
        return discountType == null || Arrays.stream(DiscountType.values())
                .anyMatch(type -> type.name().equals(discountType));
    }
}
