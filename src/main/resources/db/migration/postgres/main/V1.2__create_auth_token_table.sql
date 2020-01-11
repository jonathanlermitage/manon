--
-- setup: table to store authentication tokens (JWT)
--

create table auth_token
(
    id bigserial not null
        constraint auth_token_pkey
        primary key,
    creation_date timestamp not null,
    expiration_date timestamp not null,
    username varchar(24) not null
);
