drop table if EXISTS items_requests cascade;
drop table if EXISTS bookings cascade;
drop table if EXISTS comments cascade;
drop table if EXISTS items cascade;
drop table if EXISTS users cascade;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,

    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items_requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(4000) NOT NULL,
    requester_id BIGINT,
    created  TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_item_request PRIMARY KEY (id),
    CONSTRAINT fk_item_request_on_requester FOREIGN KEY (requester_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    description VARCHAR(4000) NOT NULL,
    available BOOLEAN NOT NULL,
    owner BIGINT NOT NULL,
    request_id BIGINT,

    CONSTRAINT pk_item PRIMARY KEY (item_id),
    CONSTRAINT fk_item_on_owner FOREIGN KEY (owner) REFERENCES users(user_id),

    CONSTRAINT fk_item_on_request FOREIGN KEY(request_id) REFERENCES items_requests(id),
    CONSTRAINT uq_owner_item_name UNIQUE(owner, item_name)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_booking TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_booking TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker BIGINT NOT NULL,
    status VARCHAR (100) NOT NULL,

    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_on_booker FOREIGN KEY(booker) REFERENCES users(user_id),
    CONSTRAINT fk_booking_on_item FOREIGN KEY(item_id) REFERENCES items(item_id)
    );

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text VARCHAR(4000) NOT NULL,
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comment_on_author FOREIGN KEY(author_id) REFERENCES users(user_id),
    CONSTRAINT fk_comment_on_item FOREIGN KEY(item_id) REFERENCES items(item_id)
)