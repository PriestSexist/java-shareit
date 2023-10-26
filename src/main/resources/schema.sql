CREATE TABLE IF NOT EXISTS users (
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL unique,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  VARCHAR(512)                            NOT NULL,
    is_available BOOLEAN                                 NOT NULL,
    owner_id     BIGINT                                  NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT ITEMS_USERS_ID_FK FOREIGN KEY (owner_id) REFERENCES USERS ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKING
(
    ID         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    START_DATE TIMESTAMP WITH TIME ZONE                not null,
    END_DATE   TIMESTAMP WITH TIME ZONE                not null,
    ITEM_ID    INTEGER                                 not null,
    BOOKER_ID  INTEGER                                 not null,
    STATUS     CHARACTER VARYING(64),
    constraint "Booking_pk"
        primary key (ID),
    constraint BOOKING_USERS_ID_FK
        foreign key (BOOKER_ID) references USERS
            on update cascade on delete cascade,
    constraint "Booking_ITEMS_null_fk"
        foreign key (ITEM_ID) references ITEMS
            on update cascade on delete cascade
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    ID            INTEGER auto_increment,
    TEXT          CHARACTER VARYING(2048),
    ITEM_ID       INTEGER,
    AUTHOR_ID     INTEGER,
    CREATION_DATE TIMESTAMP WITH TIME ZONE,
    constraint "COMMENTS_pk"
        primary key (ID),
    constraint "COMMENTS_ITEMS_null_fk"
        foreign key (ITEM_ID) references ITEMS
            on update cascade on delete cascade,
    constraint COMMENTS_USERS_ID_FK
        foreign key (AUTHOR_ID) references USERS
            on update cascade on delete cascade
);

create table ITEM_REQUEST
(
    ID          INTEGER auto_increment,
    DESCRIPTION CHARACTER VARYING(512)   not null,
    CREATED     TIMESTAMP WITH TIME ZONE not null,
    USER_ID     INTEGER,
    constraint "ITEM_REQUEST_pk"
        primary key (ID),
    constraint ITEM_REQUEST_USERS_ID_FK
        foreign key (USER_ID) references USERS
);

create table IF NOT EXISTS ITEM_ITEM_REQUEST_CONNECTION
(
    ID         INTEGER auto_increment,
    ITEM_ID    INTEGER not null,
    REQUEST_ID INTEGER,
    constraint "ITEM_ITEM_REQUEST_CONNECTION_ITEMS_null_fk"
        foreign key (ITEM_ID) references ITEMS
            on update cascade on delete cascade,
    constraint ITEM_ITEM_REQUEST_CONNECTION_ITEM_REQUEST_ID_FK
        foreign key (REQUEST_ID) references ITEM_REQUEST
            on update cascade on delete cascade
);


