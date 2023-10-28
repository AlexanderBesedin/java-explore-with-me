DROP TABLE IF EXISTS users, categories, locations, events, compilations,
participation, comments, events_compilations CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(250) NOT NULL,
    email   VARCHAR(254) NOT NULL CONSTRAINT unique_email UNIQUE
    CONSTRAINT user_const CHECK (name <> '' AND email <> '')
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50) NOT NULL CONSTRAINT uq_name UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat         REAL NOT NULL,
    lon         REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    id                    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    initiator_id          BIGINT                      NOT NULL,
    category_id           BIGINT                      NOT NULL,
    location_id           BIGINT                      NOT NULL,
    title                 VARCHAR(120)                NOT NULL,
    annotation            VARCHAR(2000)               NOT NULL,
    description           VARCHAR(7000)               NOT NULL,
    state                 VARCHAR(60)                 NOT NULL,
    date                  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    create_date           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    publish_date          TIMESTAMP WITHOUT TIME ZONE,
    participant_limit     INTEGER DEFAULT 0,
    is_paid               BOOLEAN DEFAULT FALSE,
    is_request_moderation BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_event_initiator FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_event_location FOREIGN KEY (location_id) REFERENCES locations (id)
);

CREATE TABLE IF NOT EXISTS participation (
    id                             BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    requester_id                   BIGINT                      NOT NULL,
    event_id                       BIGINT                      NOT NULL,
    status                         VARCHAR(60)                 NOT NULL,
    created_date                   TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_participation_requester FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT fk_participation_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT uc_unique_requester_event UNIQUE (requester_id, event_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    title          VARCHAR(255) NOT NULL,
    is_pinned      BOOLEAN      NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT,
    event_id       BIGINT,

    CONSTRAINT fk_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id),

    PRIMARY KEY (compilation_id, event_id)
);
