# Search Service

Сервис поиска Student Discounts. Отвечает за поиск скидок, ленту и карту. Поддерживает собственную поисковую базу по событиям `CatalogChanged` из Catalog Service.

**Стек** — Java 25, Spring Boot 4.1.2, Gradle Groovy, Spring JDBC, PostgreSQL 17 + PostGIS 3.5, Flyway и Kafka.

Укажите `SEARCH_DB_HOST` при запуске с удалённой БД или в Kubernetes; по умолчанию используется `127.0.0.1`. Остальные параметры подключения: `SEARCH_DB_PORT`, `SEARCH_DB_NAME`, `SEARCH_DB_USER`, `SEARCH_DB_PASSWORD`.

**Статус** — в `main` опубликован начальный каркас. Разработка ведётся в `dev`.