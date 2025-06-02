-- TripBook Supabase Database Schema
-- Execute these SQL commands in your Supabase SQL Editor to create the required tables

-- Enable UUID extension for auto-generating IDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create trips table
CREATE TABLE trips (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    destination VARCHAR(255) NOT NULL,
    travelers INTEGER NOT NULL DEFAULT 1,
    budget INTEGER DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANNED',
    category VARCHAR(50) NOT NULL DEFAULT 'CULTURAL',
    description TEXT DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create travel_companions table
CREATE TABLE travel_companions (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    trip_id UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) DEFAULT '',
    phone VARCHAR(50) DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for better performance
CREATE INDEX idx_trips_status ON trips(status);
CREATE INDEX idx_trips_category ON trips(category);
CREATE INDEX idx_trips_start_date ON trips(start_date);
CREATE INDEX idx_travel_companions_trip_id ON travel_companions(trip_id);

-- Add constraints for status and category enums
ALTER TABLE trips ADD CONSTRAINT check_status 
    CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED'));

ALTER TABLE trips ADD CONSTRAINT check_category 
    CHECK (category IN ('CULTURAL', 'ADVENTURE', 'RELAXATION', 'BUSINESS', 'FAMILY', 'ROMANTIC', 'WILDLIFE', 'HISTORICAL'));

-- Add constraint for date validation
ALTER TABLE trips ADD CONSTRAINT check_dates 
    CHECK (end_date >= start_date);

-- Add constraint for positive values
ALTER TABLE trips ADD CONSTRAINT check_travelers 
    CHECK (travelers > 0);

ALTER TABLE trips ADD CONSTRAINT check_budget 
    CHECK (budget >= 0);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_trips_updated_at 
    BEFORE UPDATE ON trips 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Enable Row Level Security (RLS) for basic security
ALTER TABLE trips ENABLE ROW LEVEL SECURITY;
ALTER TABLE travel_companions ENABLE ROW LEVEL SECURITY;

-- Create policies for public access (adjust based on your authentication needs)
-- For now, allowing all operations for development
CREATE POLICY "Allow all operations on trips" ON trips
    FOR ALL USING (true) WITH CHECK (true);

CREATE POLICY "Allow all operations on travel_companions" ON travel_companions
    FOR ALL USING (true) WITH CHECK (true);

-- Optional: Create a view for trips with companion count
CREATE VIEW trips_with_companion_count AS
SELECT 
    t.*,
    COALESCE(c.companion_count, 0) as companion_count
FROM trips t
LEFT JOIN (
    SELECT 
        trip_id, 
        COUNT(*) as companion_count
    FROM travel_companions 
    GROUP BY trip_id
) c ON t.id = c.trip_id;

-- Sample data insertion (optional - remove if you want to start with empty database)
-- INSERT INTO trips (name, start_date, end_date, destination, travelers, budget, status, category, description)
-- VALUES 
--     ('Test Safari Adventure', '2024-12-15', '2024-12-22', 'Kenya, Tanzania', 4, 2400, 'PLANNED', 'WILDLIFE', 'An amazing wildlife safari experience'),
--     ('Test Morocco Discovery', '2025-01-10', '2025-01-18', 'Marrakech, Fez', 2, 1800, 'PLANNED', 'CULTURAL', 'Exploring the rich culture of Morocco');

-- Grant necessary permissions (adjust based on your setup)
-- GRANT ALL ON trips TO anon, authenticated;
-- GRANT ALL ON travel_companions TO anon, authenticated;
-- GRANT USAGE ON SEQUENCE trips_id_seq TO anon, authenticated;
-- GRANT USAGE ON SEQUENCE travel_companions_id_seq TO anon, authenticated;
