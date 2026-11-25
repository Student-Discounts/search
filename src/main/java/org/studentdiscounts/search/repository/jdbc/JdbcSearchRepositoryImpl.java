package org.studentdiscounts.search.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.studentdiscounts.search.model.*;
import org.studentdiscounts.search.repository.SearchRepository;
import org.studentdiscounts.search.repository.jdbc.sql.SearchSql;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcSearchRepositoryImpl implements SearchRepository {

    private static final TypeReference<List<ScheduleGroup>> SCHEDULE_TYPE = new TypeReference<>() {};

    private final JdbcClient jdbcClient;
    private final JsonMapper jsonMapper;

    @Override
    public List<SearchPlaceWithOffers> findPlacesWithOffers(String q, String city, String categoryCode, String discountType, int limit, long offset) {
        List<SearchPlaceWithOffers> places = new ArrayList<>();
        Map<UUID, List<SearchOffer>> offersByPlace = new HashMap<>();

        jdbcClient.sql(SearchSql.FIND_PLACES_WITH_OFFERS)
                .param("q", q)
                .param("city", city)
                .param("categoryCode", categoryCode)
                .param("discountType", discountType)
                .param("limit", limit)
                .param("offset", offset)
                .query(rs -> {
                    UUID placeId = rs.getObject("place_id", UUID.class);
                    List<SearchOffer> offers = offersByPlace.get(placeId);

                    if (offers == null) {
                        offers = new ArrayList<>();
                        offersByPlace.put(placeId, offers);

                        places.add(new SearchPlaceWithOffers(
                                mapPlace(rs, placeId),
                                Collections.unmodifiableList(offers)
                        ));
                    }

                    offers.add(mapOffer(rs, placeId));
                });

        return places;
    }

    @Override
    public boolean tryAdvanceRevision(SearchSyncState syncState) {
        return jdbcClient.sql(SearchSql.ADVANCE_REVISION)
                .param("entityType", syncState.entityType().name())
                .param("entityId", syncState.entityId())
                .param("revision", syncState.revision())
                .param("deleted", syncState.deleted())
                .update() == 1;
    }

    @Override
    public void upsertPlace(SearchPlace place) {
        jdbcClient.sql(SearchSql.UPSERT_PLACE)
                .param("placeId", place.placeId())
                .param("placeName", place.name())
                .param("city", place.city())
                .param("addressLine", place.addressLine())
                .param("longitude", place.location().longitude())
                .param("latitude", place.location().latitude())
                .param("categoryCode", place.categoryCode())
                .param("categoryName", place.categoryName())
                .param("timezone", place.timezone())
                .update();

    }

    @Override
    public void upsertOffer(SearchOffer offer) {
        jdbcClient.sql(SearchSql.UPSERT_OFFER)
                .param("offerId", offer.offerId())
                .param("placeId", offer.placeId())
                .param("discountType", offer.discountType().name())
                .param("discountValue", offer.discountValue())
                .param("lastVerifiedAt", offer.lastVerifiedAt())
                .param("validFrom", offer.validFrom())
                .param("validUntil", offer.validUntil())
                .param("schedule", jsonMapper.writeValueAsString(offer.schedule()))
                .update();
    }

    @Override
    public void deleteOffer(UUID offerId) {
        jdbcClient.sql(SearchSql.DELETE_OFFER)
                .param("offerId", offerId)
                .update();
    }

    private static SearchPlace mapPlace(ResultSet rs, UUID placeId) throws SQLException {
        return new SearchPlace(
                placeId,
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("address_line"),
                new Location(rs.getDouble("latitude"), rs.getDouble("longitude")),
                rs.getString("category_code"),
                rs.getString("category_name"),
                rs.getString("timezone")
        );
    }

    private SearchOffer mapOffer(ResultSet rs, UUID placeId) throws SQLException {
        return new SearchOffer(
                rs.getObject("offer_id", UUID.class),
                placeId,
                DiscountType.valueOf(rs.getString("discount_type")),
                rs.getBigDecimal("discount_value"),
                rs.getObject("last_verified_at", OffsetDateTime.class),
                rs.getObject("valid_from", LocalDate.class),
                rs.getObject("valid_until", LocalDate.class),
                jsonMapper.readValue(
                        rs.getString("schedule_preview"),
                        SCHEDULE_TYPE
                )
        );
    }
}
