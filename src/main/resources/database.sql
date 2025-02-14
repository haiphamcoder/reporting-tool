create table
    users (
        id bigint auto_increment primary key,
        username varchar(255) not null,
        password varchar(255) not null,
        email varchar(255) not null,
        enabled tinyint (1) not null,
        role varchar(255) not null,
        created_time datetime not null,
        modified_time datetime not null,
        constraint uk_email unique (email),
        constraint uk_username unique (username)
    );

create table
    tokens (
        id bigint auto_increment primary key,
        token varchar(255) not null,
        token_type varchar(255) default 'BEARER' not null,
        revoked tinyint (1) not null,
        expired tinyint (1) not null,
        user_id bigint not null,
        constraint uk_token unique (token),
        constraint fk_tokens_user foreign key (user_id) references users (id) on update cascade on delete cascade
    );

create index idx_user_id on tokens (user_id);