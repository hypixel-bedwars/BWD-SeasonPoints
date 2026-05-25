CREATE TABLE IF NOT EXISTS point_transactions (

    id BIGSERIAL PRIMARY KEY,
    season_id INTEGER NOT NULL,
    sender_uuid UUID,
    receiver_uuid UUID NOT NULL,
    amount INTEGER NOT NULL CHECK (amount > 0),
    transaction_type VARCHAR(32) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_transactions_sender
ON point_transactions(sender_uuid);

CREATE INDEX IF NOT EXISTS idx_transactions_receiver
ON point_transactions(receiver_uuid);

CREATE INDEX IF NOT EXISTS idx_transactions_created_at
ON point_transactions(created_at);

CREATE INDEX IF NOT EXISTS idx_transactions_season
ON point_transactions(season_id);