#!/bin/bash

# Set your project's drawable directory
DRAWABLE_DIR="c:/Users/NDE HURICH DILAN/AndroidStudioProjects/TripBook/app/src/main/res/drawable"

# Create directory if it doesn't exist
mkdir -p "$DRAWABLE_DIR"

# Download travel background
curl -L "https://source.unsplash.com/random/1200x800/?travel,scenic" --output "$DRAWABLE_DIR/travel_background.jpg"
echo "Downloaded travel_background.jpg"

# Array of destinations
DESTINATIONS=(
  "tokyo"
  "paris"
  "newyork"
  "rome"
  "barcelona"
  "london"
  "sydney"
  "bangkok"
  "dubai"
  "cairo"
  "rio"
  "capetown"
  "bali"
  "machupicchu"
  "santorini"
  "iceland"
  "kyoto"
  "amsterdam"
)

# Download thumbnail for each destination
for dest in "${DESTINATIONS[@]}"; do
  curl -L "https://source.unsplash.com/random/600x400/?${dest},travel" --output "$DRAWABLE_DIR/${dest}_thumbnail.jpg"
  echo "Downloaded ${dest}_thumbnail.jpg"
  # Add a small delay to avoid rate limiting
  sleep 1
done

echo "All images downloaded to $DRAWABLE_DIR"
