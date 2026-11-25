package org.studentdiscounts.search.repository;

import org.studentdiscounts.search.model.SearchOffer;
import org.studentdiscounts.search.model.SearchPlace;
import org.studentdiscounts.search.model.SearchPlaceWithOffers;
import org.studentdiscounts.search.model.SearchSyncState;

import java.util.List;
import java.util.UUID;

public interface SearchRepository {

    List<SearchPlaceWithOffers> findPlacesWithOffers(String q, String city, String categoryCode, String discountType, int limit, long offset);

    boolean tryAdvanceRevision(SearchSyncState syncState);

    void upsertPlace(SearchPlace place);

    void upsertOffer(SearchOffer offer);

    void deleteOffer(UUID offerId);
}
