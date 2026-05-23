CREATE TABLE IF NOT EXISTS season_points (
    season_id INTEGER NOT NULL,
    player_uuid UUID NOT NULL,
    points INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (season_id, player_uuid),

    FOREIGN KEY (player_uuid)
        REFERENCES players(uuid)
        ON DELETE CASCADE,

    FOREIGN KEY (season_id)
        REFERENCES seasons(id)
);