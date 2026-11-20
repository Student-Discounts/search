package org.studentdiscounts.search.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.studentdiscounts.search.dto.request.SearchFiltersRequestDto;
import org.studentdiscounts.search.dto.response.PlaceWithOffersResponseDto;
import org.studentdiscounts.search.service.SearchService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/places")
    public ResponseEntity<List<PlaceWithOffersResponseDto>> getPlaces(
            @Valid @ModelAttribute SearchFiltersRequestDto filtersRequestDto,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(searchService.getPlacesWithOffers(filtersRequestDto, limit, offset));
    }
}
