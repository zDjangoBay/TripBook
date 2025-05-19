#!/bin/bash

# Set your project's drawable directory
DRAWABLE_DIR="c:/Users/NDE HURICH DILAN/AndroidStudioProjects/TripBook/app/src/main/res/drawable"

# Check if directory exists
if [ ! -d "$DRAWABLE_DIR" ]; then
    echo "Directory $DRAWABLE_DIR does not exist!"
    exit 1
fi

# Files to remove
echo "Cleaning up image resources from $DRAWABLE_DIR..."

# Background image
if [ -f "$DRAWABLE_DIR/travel_background.jpg" ]; then
    rm "$DRAWABLE_DIR/travel_background.jpg"
    echo "Removed travel_background.jpg"
fi

# Destination thumbnails
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

# Remove each thumbnail
for dest in "${DESTINATIONS[@]}"; do
    if [ -f "$DRAWABLE_DIR/${dest}_thumbnail.jpg" ]; then
        rm "$DRAWABLE_DIR/${dest}_thumbnail.jpg"
        echo "Removed ${dest}_thumbnail.jpg"
    fi
done

# Also remove placeholder if it exists
if [ -f "$DRAWABLE_DIR/placeholder_thumbnail.jpg" ]; then
    rm "$DRAWABLE_DIR/placeholder_thumbnail.jpg"
    echo "Removed placeholder_thumbnail.jpg"
fi

echo "Cleanup complete!"
