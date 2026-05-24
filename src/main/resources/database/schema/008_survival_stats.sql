CREATE TABLE IF NOT EXISTS survival_stats (
    season_id INTEGER NOT NULL,
    player_uuid UUID NOT NULL,

    fishing_count INTEGER DEFAULT 0,
    villager_trade_count INTEGER DEFAULT 0,

    PRIMARY KEY (season_id, player_uuid)
);

CREATE TABLE IF NOT EXISTS survival_mining_stats (
    season_id INTEGER NOT NULL,
    player_uuid UUID NOT NULL,
    material VARCHAR(64) NOT NULL,

    mining_count INTEGER DEFAULT 0,
    last_mined_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (season_id, player_uuid, material)
);
