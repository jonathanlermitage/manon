--
-- setup: table to store authentication tokens (JWT)
--
-- Warning: on Unix-based systems, names are compared in a case-sensitive manner (https://mariadb.com/kb/en/library/identifier-case-sensitivity/)
--

create table auth_token
(
    id              bigint auto_increment
        primary key,
    username        varchar(24) not null,
    expiration_date datetime(6) not null, -- used to make deletion of expired tokens easy
    creation_date   datetime(6) not null
);

create index idx_auth_token_username
    on auth_token (username);
