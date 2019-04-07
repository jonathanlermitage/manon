--
-- initial setup: application
--
-- Warning: on Unix-based systems, names are compared in a case-sensitive manner (https://mariadb.com/kb/en/library/identifier-case-sensitivity/)
--

create table app_trace
(
    id            bigint auto_increment
        primary key,
    app_id        varchar(255)  not null,
    creation_date datetime(6)   not null,
    event         varchar(255)  not null,
    level         int           not null,
    msg           varchar(2048) null
);

create table user
(
    id                 bigint auto_increment
        primary key,
    authorities        varchar(255) not null,
    creation_date      datetime(6)  not null,
    email              varchar(256) null,
    nickname           varchar(24)  null,
    password           varchar(256) not null,
    registration_state varchar(255) not null,
    update_date        datetime(6)  not null,
    username           varchar(24)  not null,
    version            bigint       not null,
    constraint uk_ob8kqyqqgmefl0aco34akdtpe
        unique (email),
    constraint uk_sb8bbouer5wak8vyiiy4pf2bx
        unique (username)
);

create table friendship
(
    id              bigint auto_increment
        primary key,
    creation_date   datetime(6) not null,
    request_from_id bigint      not null,
    request_to_id   bigint      not null,
    constraint fk7tv9o7v3idnrnkenkklfahxw6
        foreign key (request_to_id) references user (id),
    constraint fktr9d7qkl2qu3mlgg4xt1xqenw
        foreign key (request_from_id) references user (id)
);

create table friendship_event
(
    id            bigint auto_increment
        primary key,
    code          varchar(255) not null,
    creation_date datetime(6)  not null,
    friend_id     bigint       not null,
    user_id       bigint       not null,
    constraint fk8s2sne3o1l6dh8tsati2i0dfc
        foreign key (friend_id) references user (id),
    constraint fkpj2dxgfm4gtes0gay8sqikiy0
        foreign key (user_id) references user (id)
);

create table friendship_request
(
    id              bigint auto_increment
        primary key,
    creation_date   datetime(6) not null,
    request_from_id bigint      not null,
    request_to_id   bigint      not null,
    constraint fk3tpudm67c2uu564ak199779va
        foreign key (request_to_id) references user (id),
    constraint fkhtafgmony5je6ei5v2pqq04rw
        foreign key (request_from_id) references user (id)
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
    user_version            bigint       null,
    user_id                 bigint       null,
    constraint fkdn8mnv26gj3an6kom0tif95dk
        foreign key (user_id) references user (id)
);

create table user_stats
(
    id            bigint auto_increment
        primary key,
    creation_date datetime(6) not null,
    nb_users      bigint      not null
);
