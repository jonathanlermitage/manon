--
-- initial setup: application
--

create table user_
(
    id bigserial not null
        constraint user__pkey
        primary key,
    authorities varchar(255) not null,
    creation_date timestamp not null,
    email varchar(255),
    nickname varchar(24),
    password varchar(256) not null,
    registration_state varchar(255) not null,
    username varchar(24) not null
        constraint uk_wqsqlvajcne4rlyosglqglhk
        unique
);

create table friendship
(
    id bigserial not null
        constraint friendship_pkey
        primary key,
    creation_date timestamp not null,
    request_from_id bigint not null
        constraint fkqgmbu6b70ulnwdjni4lwdthe2
        references user_,
    request_to_id bigint not null
        constraint fkc6snf6d9h5db96mx1impkbxjn
        references user_
);

create table friendship_event
(
    id bigserial not null
        constraint friendship_event_pkey
        primary key,
    code varchar(255) not null,
    creation_date timestamp not null,
    friend_id bigint not null
        constraint fk8ushfmg0r5vqwjr9ahw3n8kvg
        references user_,
    user_id bigint not null
        constraint fk12vj1rg4p8lb0jblwdsqbawde
        references user_
);

create table friendship_request
(
    id bigserial not null
        constraint friendship_request_pkey
        primary key,
    creation_date timestamp not null,
    request_from_id bigint not null
        constraint fk8o995ft0wm5twx422aqu69fhk
        references user_,
    request_to_id bigint not null
        constraint fka7n4sp1dq46v9lyc84hwpqhks
        references user_
);

create table user_snapshot
(
    id bigserial not null
        constraint user_snapshot_pkey
        primary key,
    creation_date timestamp not null,
    user_authorities varchar(255),
    user_email varchar(255),
    user_nickname varchar(255),
    user_password varchar(255),
    user_registration_state integer,
    user_username varchar(255),
    user_version bigint,
    user_id bigint
        constraint fk7l1j3c8wa25v1g22csmhjghym
        references user_
);

create table user_stats
(
    id bigserial not null
        constraint user_stats_pkey
        primary key,
    creation_date timestamp not null,
    nb_users bigint not null
);
