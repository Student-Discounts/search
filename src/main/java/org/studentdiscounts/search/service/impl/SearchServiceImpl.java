package org.studentdiscounts.search.service.impl;

import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.studentdiscounts.search.dto.request.SearchFiltersRequestDto;
import org.studentdiscounts.search.dto.response.OfferResponseDto;
import org.studentdiscounts.search.dto.response.PlaceWithOffersResponseDto;
import org.studentdiscounts.search.dto.response.ScheduleGroupDto;
import org.studentdiscounts.search.model.ScheduleGroup;
import org.studentdiscounts.search.model.SearchOffer;
import org.studentdiscounts.search.model.SearchPlace;
import org.studentdiscounts.search.model.SearchPlaceWithOffers;
import org.studentdiscounts.search.repository.SearchRepository;
import org.studentdiscounts.search.service.SearchService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchRepository searchRepository;

    @Override
    public List<PlaceWithOffersResponseDto> getPlacesWithOffers(SearchFiltersRequestDto filtersRequestDto, int limit, int offset) {

        String rawQuery = filtersRequestDto.q();
        String q = StringUtils.hasText(rawQuery) ? rawQuery.strip() : null;

        return searchRepository.findPlacesWithOffers(
                        q,
                        filtersRequestDto.city(),
                        filtersRequestDto.categoryCode(),
                        filtersRequestDto.discountType(),
                        limit, offset).stream()
                .map(this::toPlaceResponse)
                .toList();
    }

    private PlaceWithOffersResponseDto toPlaceResponse(SearchPlaceWithOffers placeWithOffers) {
        SearchPlace place = placeWithOffers.place();
        List<OfferResponseDto> offers = placeWithOffers.offers().stream()
                .map(this::toOfferResponse)
                .toList();

        return new PlaceWithOffersResponseDto(
                place.placeId(),
                place.name(),
                place.city(),
                place.addressLine(),
                place.categoryCode(),
                place.categoryName(),
                offers
        );
    }

    private OfferResponseDto toOfferResponse(SearchOffer offer) {
        List<ScheduleGroupDto> schedule = offer.schedule().stream()
                .map(this::toScheduleGroupDto)
                .toList();

        return new OfferResponseDto(
                offer.offerId(),
                offer.discountType().name(),
                offer.discountValue(),
                offer.lastVerifiedAt(),
                offer.validFrom(),
                offer.validUntil(),
                schedule
        );
    }

    private ScheduleGroupDto toScheduleGroupDto(ScheduleGroup group) {
        List<ScheduleGroupDto.IntervalDto> intervals = group.intervals().stream()
                .map(interval -> new ScheduleGroupDto.IntervalDto(
                        interval.timeFrom(),
                        interval.timeUntil()
                ))
                .toList();

        return new ScheduleGroupDto(group.daysOfWeek(), intervals);
    }
}
