-- Create itinerary_items table for TripBook app
-- This table stores detailed itinerary items for each trip

CREATE TABLE IF NOT EXISTS itinerary_items (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    trip_id UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    date DATE NOT NULL,
    time VARCHAR(10) NOT NULL, -- Format: "HH:MM"
    title VARCHAR(255) NOT NULL,
    location VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('ACTIVITY', 'ACCOMMODATION', 'TRANSPORTATION')),
    notes TEXT DEFAULT '',
    description TEXT DEFAULT '',
    duration VARCHAR(100) DEFAULT '', -- e.g., "2 hours", "All day"
    cost DECIMAL(10,2) DEFAULT 0.0,
    is_completed BOOLEAN DEFAULT FALSE,
    
    -- Location coordinates for map integration
    latitude DECIMAL(10,8) NULL,
    longitude DECIMAL(11,8) NULL,
    address VARCHAR(500) DEFAULT '',
    place_id VARCHAR(255) DEFAULT '', -- For Google Places API
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_itinerary_items_trip_id ON itinerary_items(trip_id);
CREATE INDEX IF NOT EXISTS idx_itinerary_items_date ON itinerary_items(date);
CREATE INDEX IF NOT EXISTS idx_itinerary_items_trip_date ON itinerary_items(trip_id, date);

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_itinerary_items_updated_at 
    BEFORE UPDATE ON itinerary_items 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Enable Row Level Security (RLS)
ALTER TABLE itinerary_items ENABLE ROW LEVEL SECURITY;

-- Create RLS policies (adjust based on your authentication setup)
-- For now, allowing all operations - you should restrict this based on user authentication
CREATE POLICY "Allow all operations on itinerary_items" ON itinerary_items
    FOR ALL USING (true);

-- Sample data for testing (optional - remove in production)
-- INSERT INTO itinerary_items (trip_id, date, time, title, location, type, description, duration, cost) VALUES
-- ('your-trip-id-here', '2024-03-15', '09:00', 'Safari Game Drive', 'Serengeti National Park', 'ACTIVITY', 'Morning game drive to see the Big Five', '4 hours', 150.00),
-- ('your-trip-id-here', '2024-03-15', '14:00', 'Lunch at Safari Lodge', 'Serengeti Safari Lodge', 'ACTIVITY', 'Traditional African cuisine', '1 hour', 25.00),
-- ('your-trip-id-here', '2024-03-15', '20:00', 'Overnight at Safari Lodge', 'Serengeti Safari Lodge', 'ACCOMMODATION', 'Luxury tented accommodation', 'Overnight', 200.00);

-- Grant necessary permissions (adjust based on your setup)
-- GRANT ALL ON itinerary_items TO authenticated;
-- GRANT ALL ON itinerary_items TO anon;
