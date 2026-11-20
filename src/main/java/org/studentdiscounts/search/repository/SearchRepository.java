package org.studentdiscounts.search.repository;

import org.studentdiscounts.search.model.SearchPlace;

import java.util.List;

public interface SearchRepository {

    List<SearchPlace> findPlacesWithOffers(String q, String city, String categoryCode, String discountType, int limit, long offset);
}
