package org.studentdiscounts.search.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.studentdiscounts.search.model.DiscountType;
import org.studentdiscounts.search.model.ScheduleGroup;
import org.studentdiscounts.search.model.SearchOffer;
import org.studentdiscounts.search.model.SearchPlace;
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
    public List<SearchPlace> findPlacesWithOffers(String q, String city, String categoryCode, String discountType, int limit, long offset) {
        List<SearchPlace> places = new ArrayList<>();
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

                        places.add(mapPlace(
                                rs,
                                placeId,
                                Collections.unmodifiableList(offers)
                        ));
                    }

                    offers.add(mapOffer(rs, placeId));
                });

        return places;
    }

    private static SearchPlace mapPlace(ResultSet rs, UUID placeId, List<SearchOffer> offers) throws SQLException {
        return new SearchPlace(
                placeId,
                rs.getString("name"),
                rs.getString("city"),
                rs.getString("address_line"),
                rs.getString("category_code"),
                rs.getString("category_name"),
                offers
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
