--
-- initial setup: application
--
-- generated by JPA thanks to these properties:
-- spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=schema.sql
-- spring.jpa.properties.javax.persistence.schema-generation.scripts.action=create
-- (see https://thoughts-on-java.org/standardized-schema-generation-data-loading-jpa-2-1/)
--

create table auth_token
(
    id              bigint generated by default as identity (start with 1),
    creation_date   timestamp   not null,
    expiration_date timestamp   not null,
    username        varchar(24) not null,
    primary key (id)
);
create table friendship
(
    id              bigint generated by default as identity (start with 1),
    creation_date   timestamp not null,
    request_from_id bigint    not null,
    request_to_id   bigint    not null,
    primary key (id)
);
create table friendship_event
(
    id            bigint generated by default as identity (start with 1),
    code          varchar(255) not null,
    creation_date timestamp    not null,
    friend_id     bigint       not null,
    user_id       bigint       not null,
    primary key (id)
);
create table friendship_request
(
    id              bigint generated by default as identity (start with 1),
    creation_date   timestamp not null,
    request_from_id bigint    not null,
    request_to_id   bigint    not null,
    primary key (id)
);
create table user_
(
    id                 bigint generated by default as identity (start with 1),
    authorities        varchar(255) not null,
    creation_date      timestamp    not null,
    email              varchar(256),
    nickname           varchar(24),
    password           varchar(256) not null,
    registration_state varchar(255) not null,
    update_date        timestamp    not null,
    username           varchar(24)  not null,
    version            bigint       not null,
    primary key (id)
);
create table user_snapshot
(
    id                      bigint generated by default as identity (start with 1),
    creation_date           timestamp not null,
    user_authorities        varchar(255),
    user_email              varchar(255),
    user_nickname           varchar(255),
    user_password           varchar(255),
    user_registration_state integer,
    user_username           varchar(255),
    user_version            bigint,
    user_id                 bigint,
    primary key (id)
);
create table user_stats
(
    id            bigint generated by default as identity (start with 1),
    creation_date timestamp not null,
    nb_users      bigint    not null,
    primary key (id)
);
alter table user_
    add constraint UK_sb8bbouer5wak8vyiiy4pf2bx unique (username);
alter table friendship
    add constraint FKtr9d7qkl2qu3mlgg4xt1xqenw foreign key (request_from_id) references user_;
alter table friendship
    add constraint FK7tv9o7v3idnrnkenkklfahxw6 foreign key (request_to_id) references user_;
alter table friendship_event
    add constraint FK8s2sne3o1l6dh8tsati2i0dfc foreign key (friend_id) references user_;
alter table friendship_event
    add constraint FKpj2dxgfm4gtes0gay8sqikiy0 foreign key (user_id) references user_;
alter table friendship_request
    add constraint FKhtafgmony5je6ei5v2pqq04rw foreign key (request_from_id) references user_;
alter table friendship_request
    add constraint FK3tpudm67c2uu564ak199779va foreign key (request_to_id) references user_;
alter table user_snapshot
    add constraint FKdn8mnv26gj3an6kom0tif95dk foreign key (user_id) references user_;
