package org.studentdiscounts.search.repository.jdbc.sql;

public final class SearchSql {

    public static final String FIND_PLACES_WITH_OFFERS = """
    WITH search_query AS (
        SELECT CAST(
            string_agg(CAST(array_to_tsvector(ARRAY[lexeme]) AS text) || ':*', ' & ')
            AS tsquery
        ) AS terms
        FROM unnest(
            tsvector_to_array(to_tsvector('pg_catalog.russian', :q))
        ) AS tokens(lexeme)
    ),
    valid_offers AS NOT MATERIALIZED (
        SELECT o.offer_id, o.place_id, o.discount_type, o.discount_value,
               o.last_verified_at, o.valid_from, o.valid_until, o.schedule_preview
        FROM search_offers o
        JOIN search_places p ON p.place_id = o.place_id
        WHERE daterange(o.valid_from, o.valid_until, '[]') @>
              CAST(CURRENT_TIMESTAMP AT TIME ZONE p.timezone AS date)
          AND (CAST(:discountType AS text) IS NULL
               OR o.discount_type = CAST(:discountType AS discount_type))
    ),
    place_page AS (
        SELECT p.place_id, p.place_name AS name, p.city, p.address_line,
               p.category_code, p.category_name, p.timezone, ST_Y(CAST(p.location AS geometry)) AS latitude,
               ST_X(CAST(p.location AS geometry)) AS longitude,
               COALESCE(
                   ts_rank(p.search_vector, (SELECT terms FROM search_query)),
                   0
               ) AS relevance
        FROM search_places p
        WHERE (CAST(:q AS text) IS NULL
               OR p.search_vector @@ (SELECT terms FROM search_query))
          AND (CAST(:city AS text) IS NULL OR p.city = :city)
          AND (CAST(:categoryCode AS text) IS NULL OR p.category_code = :categoryCode)
          AND EXISTS (
              SELECT 1 FROM valid_offers o WHERE o.place_id = p.place_id
          )
        ORDER BY relevance DESC, p.place_id
        LIMIT :limit OFFSET :offset
    )
    SELECT p.place_id, p.name, p.city, p.address_line,
           p.category_code, p.category_name,
           p.latitude, p.longitude, p.timezone,
           o.offer_id, o.discount_type, o.discount_value,
           o.last_verified_at, o.valid_from, o.valid_until, o.schedule_preview
    FROM place_page p
    JOIN valid_offers o ON o.place_id = p.place_id
    ORDER BY p.relevance DESC, p.place_id, o.last_verified_at DESC
    """;

    public static final String ADVANCE_REVISION = """
        INSERT INTO search_sync_state (
            entity_type, entity_id, revision, is_deleted, applied_at
        )
        VALUES (:entityType, :entityId, :revision, :deleted, CURRENT_TIMESTAMP)
        ON CONFLICT (entity_type, entity_id) DO UPDATE
        SET revision = EXCLUDED.revision,
            is_deleted = EXCLUDED.is_deleted,
            applied_at = EXCLUDED.applied_at
        WHERE search_sync_state.revision < EXCLUDED.revision
        """;

    public static final String UPSERT_PLACE = """
        INSERT INTO search_places (
            place_id, place_name, city, address_line, location,
            category_code, category_name, timezone
        )
        VALUES (
            :placeId, :placeName, :city, :addressLine,
            CAST(ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326) AS geography),
            :categoryCode, :categoryName, :timezone
        )
        ON CONFLICT (place_id) DO UPDATE
        SET place_name = EXCLUDED.place_name,
            city = EXCLUDED.city,
            address_line = EXCLUDED.address_line,
            location = EXCLUDED.location,
            category_code = EXCLUDED.category_code,
            category_name = EXCLUDED.category_name,
            timezone = EXCLUDED.timezone
        """;

    public static final String UPSERT_OFFER = """
        INSERT INTO search_offers (
            offer_id, place_id, discount_type, discount_value,
            last_verified_at, valid_from, valid_until, schedule_preview
        )
        VALUES (
            :offerId, :placeId, CAST(:discountType AS discount_type), :discountValue,
            :lastVerifiedAt, :validFrom, :validUntil, CAST(:schedule AS jsonb)
        )
        ON CONFLICT (offer_id) DO UPDATE
        SET place_id = EXCLUDED.place_id,
            discount_type = EXCLUDED.discount_type,
            discount_value = EXCLUDED.discount_value,
            last_verified_at = EXCLUDED.last_verified_at,
            valid_from = EXCLUDED.valid_from,
            valid_until = EXCLUDED.valid_until,
            schedule_preview = EXCLUDED.schedule_preview
        """;

    public static final String DELETE_OFFER = """
        DELETE FROM search_offers WHERE offer_id = :offerId
        """;

    private SearchSql(){}
}
