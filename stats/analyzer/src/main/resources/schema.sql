CREATE TABLE IF NOT EXISTS user_event_actions (
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    weight DOUBLE PRECISION NOT NULL,
    last_action_time TIMESTAMP,
    PRIMARY KEY (user_id, event_id)
);

CREATE TABLE IF NOT EXISTS event_similarity (
    event_a BIGINT NOT NULL,
    event_b BIGINT NOT NULL,
    score DOUBLE PRECISION NOT NULL,
    last_action_time TIMESTAMP,
    PRIMARY KEY (event_a, event_b)
);
