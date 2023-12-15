CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "user"
(
    id                      uuid DEFAULT uuid_generate_v4 (),
    first_name              VARCHAR         NOT NULL,
    last_name               VARCHAR         NOT NULL,
    email                   VARCHAR         NOT NULL,
    birthday                DATE            NOT NULL,
    version                 BIGINT          NOT NULL DEFAULT 0,
    active                  BOOLEAN         NOT NULL DEFAULT true,
    PRIMARY KEY (id)
);
