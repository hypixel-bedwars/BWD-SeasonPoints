CREATE TABLE IF NOT EXISTS advancement_rewards (
    season_id INTEGER NOT NULL,
    player_uuid UUID NOT NULL,
    advancement_key VARCHAR(200) NOT NULL,
    completed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (season_id, player_uuid, advancement_key),

    FOREIGN KEY (player_uuid)
        REFERENCES players(uuid)
        ON DELETE CASCADE,

    FOREIGN KEY (season_id)
        REFERENCES seasons(id)
        ON DELETE CASCADE
);