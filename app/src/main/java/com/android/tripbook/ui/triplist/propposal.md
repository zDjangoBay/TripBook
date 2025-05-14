# Proposition for Trip Catalog Module

## Introduction
The Trip Catalog module allows users to share photos, videos, reels, and shorts of their trips. This content helps other users decide on potential trips based on shared experiences.

## Features
- *User Contributions:* Users can upload various media related to their trips.
- *Catalog Display:* All shared media is organized in a catalog format, making it easy for others to view.
- *Decision Making:* Users can browse through the catalog to make informed decisions about their travel plans.
- *Comments and Reviews:* Users can leave comments and reviews on shared media.
- *Search and Filter Options:* Users can search for trips by destination, media type, or user.

## Implementation Steps

### Step-by-Step Process
1. *Define User Interface:*
    - Create forms for users to upload media.
    - Design the catalog display page.
    - Implement search and filter functionalities.

2. *Set Up Database:*
    - Create tables for Users, Media, Trips, Catalog, Comments, and Reviews.

3. *Develop Backend Functionality:*
    - Implement API endpoints for uploading media, retrieving catalog items, and managing comments/reviews.

4. *Integrate Frontend and Backend:*
    - Connect the frontend forms and display components to the backend API.

5. *Test Functionality:*
    - Ensure all features work as intended and fix any bugs.

6. *Deployment:*
    - Deploy the module to the production environment.

7. *Documentation:*
    - Write documentation for future reference and user guidance.

## Entities and Relationships

### Entities
1. *User*
    - *Attributes:* User ID, Name, Profile Picture
    - *Description:* Represents a user who shares media.

2. *Media*
    - *Attributes:* Media ID, Type (Photo/Video), URL, Trip ID (foreign key)
    - *Description:* Represents the media shared by users.

3. *Trip*
    - *Attributes:* Trip ID, Destination, Date, User ID (foreign key)
    - *Description:* Represents the trips that users share media about.

4. *Catalog*
    - *Description:* A collection of all shared media, categorized by trips and users.

5. *Comment*
    - *Attributes:* Comment ID, Content, User ID (foreign key), Media ID (foreign key)
    - *Description:* Represents comments made by users on media.

6. *Review*
    - *Attributes:* Review ID, Rating, Content, User ID (foreign key), Trip ID (foreign key)
    - *Description:* Represents reviews given by users about trips.

### Relationships
- Each *User* can contribute multiple *Media* items.
- Each *Media* item is associated with a specific *Trip*.
- Each *Trip* is linked to a *User* who created it.
- The *Catalog* aggregates all media shared by users.
- Each *Media* can have multiple *Comments*.
- Each *Trip* can have multiple *Reviews* from different users.

## Database Entries

### Tables
1. *Users Table*
    - user_id: INT (Primary Key)
    - name: VARCHAR
    - profile_picture: VARCHAR

2. *Media Table*
    - media_id: INT (Primary Key)
    - type: ENUM('photo', 'video')
    - url: VARCHAR
    - trip_id: INT (Foreign Key referencing Trips)

3. *Trips Table*
    - trip_id: INT (Primary Key)
    - destination: VARCHAR
    - date: DATE
    - user_id: INT (Foreign Key referencing Users)

4. *Comments Table*
    - comment_id: INT (Primary Key)
    - content: TEXT
    - user_id: INT (Foreign Key referencing Users)
    - media_id: INT (Foreign Key referencing Media)

5. *Reviews Table*
    - review_id: INT (Primary Key)
    - rating: INT
    - content: TEXT
    - user_id: INT (Foreign Key referencing Users)
    - trip_id: INT (Foreign Key referencing Trips)

## Scenarios
- *Uploading Media:* Users can upload photos/videos, which are stored in the Media table linked to the appropriate Trip.
- *Viewing Catalog:* Users can view all media associated with trips in the Catalog.
- *Searching Trips:* Users can search for trips by destination or media type.
- *Adding Comments:* Users can comment on media to provide feedback or share experiences.
- *Leaving Reviews:* Users can rate and review trips to help others in their decision-making process.

## Commit Plan
1. *Create database schema for Users*
    - Commit Message: "Add Users table to database"

2. *Create database schema for Media*
    - Commit Message: "Add Media table to database"

3. *Create database schema for Trips*
    - Commit Message: "Add Trips table to database"

4. *Create database schema for Comments*
    - Commit Message: "Add Comments table to database"

5. *Create database schema for Reviews*
    - Commit Message: "Add Reviews table to database"

6. *Implement API for uploading media*
    - Commit Message: "Add API endpoint for media upload"

7. *Implement API for retrieving catalog*
    - Commit Message: "Add API endpoint for catalog retrieval"

8. *Implement API for comments and reviews*
    - Commit Message: "Add API endpoints for comments and reviews"

9. *Develop frontend forms for media upload*
    - Commit Message: "Create frontend form for media upload"

10. *Finalize and test module functionality*
- Commit Message: "Complete testing of Trip Catalog module"

## Conclusion
The Trip Catalog module enhances user experience by providing a platform for sharing and discovering travel experiences, helping users make informed travel decisions. This proposition outlines a comprehensive approach to develop and implement the module, ensuring a robust and user-friendly system.
