Hotel Reservation System

Overview

The Hotel Reservation System is a robust PHP-based backend API designed to streamline hotel management, room bookings, and user authentication. Built with RESTful principles, it offers secure, role-based access to various functionalities via JSON Web Tokens (JWT).

This system supports multiple user roles—Admin, Staff, and Guest—enabling differentiated access and management capabilities. Key features include comprehensive hotel and room management, reservation processing, payment tracking, and system activity logging.

Project Structure

hotels_and_room/
├── auth/                  # Authentication endpoints (login, logout, token refresh)
├── config/                # Configuration files (database, JWT secret)
├── exclusive/             # Admin-only operations (user management, dashboard)
├── handlers/              # Core business logic (hotels, rooms, reservations)
├── middleware/            # Security & utility layers (authentication, logging, rate limiting)
├── shared/                # Shared endpoints for all user roles (hotels, rooms, reservations)
├── logs/                  # Auto-generated logs
├── public/                # Public assets (if any)
└── script/                # Utility scripts (database seeding, automated tests)

Database Schema

The system’s relational database includes the following core tables:

Table	Description

users	Stores user accounts and roles
hotels	Contains hotel information
room_types	Defines categories of rooms (standard, deluxe, suite)
rooms	Individual rooms linked to hotels
staff_assignments	Maps staff members to hotels
reservations	Records bookings
room_availability	Tracks daily room availability
payments	Logs payment transactions
audit_log	System activity and audit trail
system_settings	Configurable application settings


Relationships:

One-to-many from users to reservations

One-to-many from hotels to rooms

Many-to-one from rooms to room types

One-to-one from reservations to payments

API Endpoints

Authentication

Endpoint	Method	Description

/auth/login	POST	User login; returns JWT token
/auth/logout	POST	Logs out user; invalidates token
/auth/password-reset	POST	Initiates password reset process
/auth/refresh	POST	Refreshes JWT token


Hotels

Endpoint	Method	Access	Description

/shared/hotels	GET	All	Retrieves list of hotels
/shared/hotels	POST	Admin	Creates a new hotel
/shared/hotels	PUT	Admin	Updates existing hotel details
/shared/hotels	DELETE	Admin	Soft deletes a hotel


Rooms

Endpoint	Method	Access	Description

/shared/rooms?hotel_id=X	GET	All	Lists rooms in a specified hotel
/shared/rooms	POST	Staff+	Adds a new room
/shared/rooms	PUT	Staff+	Updates room information


Reservations

Endpoint	Method	Access	Description

/shared/reservations	GET	All	Lists reservations (filtered by role)
/shared/reservations	POST	All	Creates a new booking
/shared/reservations	PUT	Staff+	Modifies an existing booking
/shared/reservations	DELETE	Staff+	Cancels a booking


Admin-Only Endpoints

Endpoint	Method	Description

/exclusive/users	GET	Lists all users
/exclusive/users	POST	Creates a user
/exclusive/users	PUT	Updates user info
/exclusive/users	DELETE	Deletes a user
/exclusive/dashboard	GET	Displays admin metrics

Testing & Database Seeding

Database Seeding:

The populate_database.php script populates the database with test data for development and testing purposes:

Creates default users (admin, staff, guest1, guest2)

Adds sample hotels from Cameroon with associated rooms

Generates room availability data spanning 90 days

Creates sample reservations


Run the script with:

php script/populate_database.php

Automated API Testing

backend_tests.php automates validation of the backend API functionality including authentication, CRUD operations, and error handling.

Execute tests via:

php script/backend_tests.php

Example test output:

=== Starting Backend Tests ===

=== Testing Authentication ===
[PASS] Admin login
[PASS] Staff login
[PASS] Guest login
[PASS] Invalid login rejection

=== Testing Hotel Operations ===
[PASS] Hotel creation (admin)
[PASS] Hotel creation rejection (staff)
[PASS] Hotel listing

=== Test Complete ===


Security Measures:

JWT Authentication: Tokens expire after 24 hours; blacklist managed with Redis for logout invalidation.

Role-Based Access Control: Enforced via middleware for endpoint protection.

Rate Limiting:

Max 5 login attempts per 5 minutes per IP

API calls limited between 100 and 1000 requests per minute depending on user role


Input Validation: All endpoints rigorously validate inputs, formats, and permissions.

Error Handling:

The API responds with standardized HTTP status codes and JSON error messages:

HTTP Code	Meaning

400	Bad Request (invalid input)
401	Unauthorized (invalid or missing JWT)
403	Forbidden (access denied)
404	Not Found (resource missing)
405	Method Not Allowed
429	Too Many Requests
500	Internal Server Error


Error Response Format:

{
  "error": "Descriptive error message"
}


Deployment Guide:

Prerequisites

PHP 7.4 or higher

MySQL 5.7 or higher

Redis (for token blacklisting)

Apache or Nginx web server


Setup Steps:

1. Import the database schema from hotel_reservation_system.sql.


2. Configure database credentials in config/database.php.


3. Set your JWT secret key in config/jwt_secret.php.


4. Ensure the logs/ directory has write permissions.


5. Deploy the application on your web server.


Troubleshooting:

Issue	Suggested Solution

Missing Authorization header	Verify Authorization: Bearer <token> is sent
HTTP 405 Method Not Allowed	Confirm correct HTTP methods for endpoints
Database connection failures	Check database credentials and server status
Redis connection errors	Install Redis or disable blacklist in auth.php


Roadmap & Future Enhancements:

Integrate email notifications for bookings and password resets

Add payment gateway integration for live transactions

Enhance hotel search with advanced filters

Implement multi-language support for broader accessibility


 Note: Always validate and test any changes using the provided scripts before deploying to a production environment.


