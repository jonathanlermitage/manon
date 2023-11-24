--
-- initial setup: application
--
-- Warning: on Unix-based systems, names are compared in a case-sensitive manner (https://mariadb.com/kb/en/library/identifier-case-sensitivity/)
--

create table user_
(
    id                 bigint auto_increment
        primary key,
    authorities        varchar(255) not null,
    creation_date      datetime(6)  not null,
    email              varchar(255) null,
    nickname           varchar(24)  null,
    password           varchar(256) not null,
    registration_state varchar(255) not null,
    username           varchar(24)  not null,
    constraint uk__user__username
        unique (username)
);

create table friendship
(
    id              bigint auto_increment
        primary key,
    creation_date   datetime(6) not null,
    request_from_id bigint      not null,
    request_to_id   bigint      not null,
    constraint fk__friendship__request_to_id
        foreign key (request_to_id) references user_ (id),
    constraint fk__friendship__request_from_id
        foreign key (request_from_id) references user_ (id)
);

create table friendship_event
(
    id            bigint auto_increment
        primary key,
    code          varchar(255) not null,
    creation_date datetime(6)  not null,
    friend_id     bigint       not null,
    user_id       bigint       not null,
    constraint fk__friendship_event__friend_id
        foreign key (friend_id) references user_ (id),
    constraint fk__friendship_event__user_id
        foreign key (user_id) references user_ (id)
);

create table friendship_request
(
    id              bigint auto_increment
        primary key,
    creation_date   datetime(6) not null,
    request_from_id bigint      not null,
    request_to_id   bigint      not null,
    constraint fk__friendship_request__request_to_id
        foreign key (request_to_id) references user_ (id),
    constraint fk__friendship_request__request_from_id
        foreign key (request_from_id) references user_ (id)
);

create table user_snapshot
(
    id                      bigint auto_increment
        primary key,
    creation_date           datetime(6)  not null,
    user_authorities        varchar(255) null,
    user_email              varchar(255) null,
    user_nickname           varchar(255) null,
    user_password           varchar(255) null,
    user_registration_state int          null,
    user_username           varchar(255) null,
    user_id                 bigint       null,
    constraint fk__user_snapshot__user_id
        foreign key (user_id) references user_ (id)
);

create table user_stats
(
    id            bigint auto_increment
        primary key,
    creation_date datetime(6) not null,
    nb_users      bigint      not null
);
