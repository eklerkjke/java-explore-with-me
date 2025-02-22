CREATE TABLE IF NOT EXISTS stats (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app VARCHAR(100) NOT NULL,
    uri VARCHAR(100) NOT NULL,
    ip  VARCHAR(15)  NOT NULL,
    timestamp TIMESTAMP NOT NULL
);