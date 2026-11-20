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
               p.category_code, p.category_name,
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
    SELECT p.place_id, p.name, p.city, p.address_line, p.category_code, p.category_name,
           o.offer_id, o.discount_type, o.discount_value,
           o.last_verified_at, o.valid_from, o.valid_until, o.schedule_preview
    FROM place_page p
    JOIN valid_offers o ON o.place_id = p.place_id
    ORDER BY p.relevance DESC, p.place_id, o.last_verified_at DESC
    """;

    private SearchSql(){}
}
