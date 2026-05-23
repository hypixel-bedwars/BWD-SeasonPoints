CREATE TABLE IF NOT EXISTS biome_discoveries (
    season_id INTEGER NOT NULL,
    player_uuid UUID NOT NULL,
    biome_key VARCHAR(100) NOT NULL,
    discovered_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (season_id, player_uuid, biome_key),

    FOREIGN KEY (player_uuid)
        REFERENCES players(uuid)
        ON DELETE CASCADE,
        
    FOREIGN KEY (season_id)
        REFERENCES seasons(id)
        ON DELETE CASCADE
);