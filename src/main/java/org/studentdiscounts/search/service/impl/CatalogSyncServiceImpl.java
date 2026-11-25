package org.studentdiscounts.search.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.studentdiscounts.search.kafka.event.CatalogChangedEvent;
import org.studentdiscounts.search.kafka.event.payload.OfferSnapshot;
import org.studentdiscounts.search.kafka.event.payload.PlaceSnapshot;
import org.studentdiscounts.search.model.SearchOffer;
import org.studentdiscounts.search.model.SearchPlace;
import org.studentdiscounts.search.model.SearchSyncState;
import org.studentdiscounts.search.repository.SearchRepository;
import org.studentdiscounts.search.service.CatalogSyncService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogSyncServiceImpl implements CatalogSyncService {

    private final SearchRepository searchRepository;

    @Override
    @Transactional
    public void apply(CatalogChangedEvent catalogChangedEvent) {

        UUID placeId = catalogChangedEvent.placeId();
        catalogChangedEvent.data().place().ifPresent(snapshot -> applyPlace(placeId, snapshot));

        for (OfferSnapshot offer : catalogChangedEvent.data().offers()){
            applyOffer(placeId, offer);
        }
    }

    private void applyPlace(UUID placeId, PlaceSnapshot snapshot){
        var syncState = new SearchSyncState(
                SearchSyncState.EntityType.PLACE,
                placeId,
                snapshot.revision(),
                false
        );

        if(!searchRepository.tryAdvanceRevision(syncState)) return;

        searchRepository.upsertPlace(new SearchPlace(
                placeId,
                snapshot.placeName(),
                snapshot.city(),
                snapshot.addressLine(),
                snapshot.location(),
                snapshot.categoryCode(),
                snapshot.categoryName(),
                snapshot.timezone()
        ));
    }

    private void applyOffer(UUID placeId, OfferSnapshot snapshot){
        boolean deleted = snapshot.status() != OfferSnapshot.OfferStatus.ACTIVE;

        var syncState = new SearchSyncState(
                SearchSyncState.EntityType.OFFER,
                snapshot.offerId(),
                snapshot.revision(),
                deleted
        );

        if(!searchRepository.tryAdvanceRevision(syncState)) return;

        if(deleted) {
            searchRepository.deleteOffer(snapshot.offerId());
            return;
        }

        searchRepository.upsertOffer(new SearchOffer(
                snapshot.offerId(),
                placeId,
                snapshot.discountType(),
                snapshot.discountValue(),
                snapshot.lastVerifiedAt(),
                snapshot.validFrom(),
                snapshot.validUntil(),
                snapshot.schedule()

        ));
    }
}
