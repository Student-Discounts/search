package org.studentdiscounts.search.service;

import org.studentdiscounts.search.dto.request.SearchFiltersRequestDto;
import org.studentdiscounts.search.dto.response.PlaceWithOffersResponseDto;

import java.util.List;

public interface SearchService {

    List<PlaceWithOffersResponseDto> getPlacesWithOffers(SearchFiltersRequestDto filtersRequestDto, int limit, int offset);
}
