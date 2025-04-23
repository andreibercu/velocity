CREATE TABLE birds (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(255) NOT NULL,
    weight DOUBLE PRECISION,
    height DOUBLE PRECISION,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL
);

CREATE TABLE sightings (
    id BIGSERIAL PRIMARY KEY,
    bird_id BIGINT NOT NULL,
    location VARCHAR(255) NOT NULL,
    datetime TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL,

    CONSTRAINT fk_sighting_bird
        FOREIGN KEY (bird_id) REFERENCES birds(id)
);