-- Fix NULL values in itinerary_items table
-- Run this script if you already have the table created and it contains NULL values

-- Update any existing NULL values to empty strings or default values
UPDATE itinerary_items 
SET 
    notes = COALESCE(notes, ''),
    description = COALESCE(description, ''),
    duration = COALESCE(duration, ''),
    cost = COALESCE(cost, 0.0),
    is_completed = COALESCE(is_completed, FALSE),
    address = COALESCE(address, ''),
    place_id = COALESCE(place_id, '')
WHERE 
    notes IS NULL 
    OR description IS NULL 
    OR duration IS NULL 
    OR cost IS NULL 
    OR is_completed IS NULL 
    OR address IS NULL 
    OR place_id IS NULL;

-- Alter table to add NOT NULL constraints (if not already present)
-- Note: Only run these if the table was created without NOT NULL constraints

-- ALTER TABLE itinerary_items ALTER COLUMN notes SET NOT NULL;
-- ALTER TABLE itinerary_items ALTER COLUMN notes SET DEFAULT '';

-- ALTER TABLE itinerary_items ALTER COLUMN description SET NOT NULL;
-- ALTER TABLE itinerary_items ALTER COLUMN description SET DEFAULT '';

-- ALTER TABLE itinerary_items ALTER COLUMN duration SET NOT NULL;
-- ALTER TABLE itinerary_items ALTER COLUMN duration SET DEFAULT '';

-- ALTER TABLE itinerary_items ALTER COLUMN cost SET NOT NULL;
-- ALTER TABLE itinerary_items ALTER COLUMN cost SET DEFAULT 0.0;

-- ALTER TABLE itinerary_items ALTER COLUMN is_completed SET NOT NULL;
-- ALTER TABLE itinerary_items ALTER COLUMN is_completed SET DEFAULT FALSE;

-- ALTER TABLE itinerary_items ALTER COLUMN address SET NOT NULL;
-- ALTER TABLE itinerary_items ALTER COLUMN address SET DEFAULT '';

-- ALTER TABLE itinerary_items ALTER COLUMN place_id SET NOT NULL;
-- ALTER TABLE itinerary_items ALTER COLUMN place_id SET DEFAULT '';

-- Verify the changes
SELECT 
    COUNT(*) as total_rows,
    COUNT(CASE WHEN notes IS NULL THEN 1 END) as null_notes,
    COUNT(CASE WHEN description IS NULL THEN 1 END) as null_description,
    COUNT(CASE WHEN duration IS NULL THEN 1 END) as null_duration,
    COUNT(CASE WHEN cost IS NULL THEN 1 END) as null_cost,
    COUNT(CASE WHEN is_completed IS NULL THEN 1 END) as null_is_completed,
    COUNT(CASE WHEN address IS NULL THEN 1 END) as null_address,
    COUNT(CASE WHEN place_id IS NULL THEN 1 END) as null_place_id
FROM itinerary_items;
