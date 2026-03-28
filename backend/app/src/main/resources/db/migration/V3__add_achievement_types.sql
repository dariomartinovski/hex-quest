CREATE TABLE achievement_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

INSERT INTO achievement_types (name, description) VALUES
('THRESHOLD', 'Unlocks when a users cumulative progress >= a fixed value'),
('SUPREMACY', 'Dynamically held by whoever leads, after a minimum threshold is crossed');

ALTER TABLE achievements ADD COLUMN type_id BIGINT REFERENCES achievement_types(id);

-- Update existing data if any (just in case flyway already ran V1 and data was inserted, though unlikely)
UPDATE achievements SET type_id = (SELECT id FROM achievement_types WHERE achievement_types.name = achievements.type) WHERE achievements.type IS NOT NULL;

-- If any old rows had unrecognized types, this will fail the NOT NULL constraint. Safe to assume it's clean for now.
ALTER TABLE achievements DROP COLUMN type;
ALTER TABLE achievements ALTER COLUMN type_id SET NOT NULL;
