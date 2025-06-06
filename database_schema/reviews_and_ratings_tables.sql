-- Create reviews and ratings tables for TripBook app
-- This schema supports rating and reviewing trips, destinations, agencies, and activities

-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL, -- User identifier (could be email, UUID, etc.)
    user_name VARCHAR(255) NOT NULL, -- Display name of the reviewer
    user_avatar VARCHAR(500) DEFAULT '', -- Profile picture URL
    review_type VARCHAR(50) NOT NULL CHECK (review_type IN ('TRIP', 'DESTINATION', 'AGENCY', 'ACTIVITY')),
    target_id VARCHAR(255) NOT NULL, -- ID of the item being reviewed
    target_name VARCHAR(500) NOT NULL, -- Name of the target for display
    rating DECIMAL(2,1) NOT NULL CHECK (rating >= 1.0 AND rating <= 5.0),
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    pros TEXT[] DEFAULT '{}', -- Array of positive points
    cons TEXT[] DEFAULT '{}', -- Array of negative points
    photos TEXT[] DEFAULT '{}', -- Array of photo URLs
    helpful_count INTEGER DEFAULT 0,
    is_verified BOOLEAN DEFAULT FALSE, -- Whether reviewer actually used the service
    status VARCHAR(20) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create ratings table (for quick rating without full review)
CREATE TABLE IF NOT EXISTS ratings (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    review_type VARCHAR(50) NOT NULL CHECK (review_type IN ('TRIP', 'DESTINATION', 'AGENCY', 'ACTIVITY')),
    target_id VARCHAR(255) NOT NULL,
    rating DECIMAL(2,1) NOT NULL CHECK (rating >= 1.0 AND rating <= 5.0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Ensure one rating per user per target
    UNIQUE(user_id, review_type, target_id)
);

-- Create review_helpfulness table to track who found reviews helpful
CREATE TABLE IF NOT EXISTS review_helpfulness (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    user_id VARCHAR(255) NOT NULL,
    is_helpful BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Ensure one vote per user per review
    UNIQUE(review_id, user_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_reviews_target ON reviews(review_type, target_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_status ON reviews(status);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(rating);
CREATE INDEX IF NOT EXISTS idx_reviews_created_at ON reviews(created_at);

CREATE INDEX IF NOT EXISTS idx_ratings_target ON ratings(review_type, target_id);
CREATE INDEX IF NOT EXISTS idx_ratings_user ON ratings(user_id);

CREATE INDEX IF NOT EXISTS idx_review_helpfulness_review ON review_helpfulness(review_id);
CREATE INDEX IF NOT EXISTS idx_review_helpfulness_user ON review_helpfulness(user_id);

-- Create trigger to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_reviews_updated_at 
    BEFORE UPDATE ON reviews 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Create function to update helpful_count when review_helpfulness changes
CREATE OR REPLACE FUNCTION update_review_helpful_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.is_helpful THEN
            UPDATE reviews SET helpful_count = helpful_count + 1 WHERE id = NEW.review_id;
        END IF;
        RETURN NEW;
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.is_helpful != NEW.is_helpful THEN
            IF NEW.is_helpful THEN
                UPDATE reviews SET helpful_count = helpful_count + 1 WHERE id = NEW.review_id;
            ELSE
                UPDATE reviews SET helpful_count = helpful_count - 1 WHERE id = NEW.review_id;
            END IF;
        END IF;
        RETURN NEW;
    ELSIF TG_OP = 'DELETE' THEN
        IF OLD.is_helpful THEN
            UPDATE reviews SET helpful_count = helpful_count - 1 WHERE id = OLD.review_id;
        END IF;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_helpful_count_trigger
    AFTER INSERT OR UPDATE OR DELETE ON review_helpfulness
    FOR EACH ROW
    EXECUTE FUNCTION update_review_helpful_count();

-- Enable Row Level Security (RLS)
ALTER TABLE reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE ratings ENABLE ROW LEVEL SECURITY;
ALTER TABLE review_helpfulness ENABLE ROW LEVEL SECURITY;

-- Create RLS policies (adjust based on your authentication setup)
-- For now, allowing all operations - you should restrict this based on user authentication
CREATE POLICY "Allow all operations on reviews" ON reviews
    FOR ALL USING (true);

CREATE POLICY "Allow all operations on ratings" ON ratings
    FOR ALL USING (true);

CREATE POLICY "Allow all operations on review_helpfulness" ON review_helpfulness
    FOR ALL USING (true);

-- Create view for review summaries
CREATE OR REPLACE VIEW review_summaries AS
SELECT 
    r.review_type,
    r.target_id,
    COUNT(*) as total_reviews,
    ROUND(AVG(r.rating), 1) as average_rating,
    COUNT(CASE WHEN r.rating = 5 THEN 1 END) as five_star_count,
    COUNT(CASE WHEN r.rating = 4 THEN 1 END) as four_star_count,
    COUNT(CASE WHEN r.rating = 3 THEN 1 END) as three_star_count,
    COUNT(CASE WHEN r.rating = 2 THEN 1 END) as two_star_count,
    COUNT(CASE WHEN r.rating = 1 THEN 1 END) as one_star_count
FROM reviews r
WHERE r.status = 'APPROVED'
GROUP BY r.review_type, r.target_id;

-- Grant necessary permissions (adjust based on your setup)
-- GRANT ALL ON reviews TO authenticated;
-- GRANT ALL ON ratings TO authenticated;
-- GRANT ALL ON review_helpfulness TO authenticated;
-- GRANT SELECT ON review_summaries TO authenticated;
