CREATE TABLE IF NOT EXISTS pvp_kills (
    season_id INT NOT NULL,
    killer_uuid VARCHAR(36) NOT NULL,
    victim_uuid VARCHAR(36) NOT NULL,
    killed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (season_id, killer_uuid, victim_uuid),

    FOREIGN KEY (season_id)
        REFERENCES seasons(id)
        ON DELETE CASCADE
);
