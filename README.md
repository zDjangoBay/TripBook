# Transportation Management API

A robust REST API for managing different types of transportation vehicles (planes, cars, and ships) built with Spring Boot and Kotlin.

## Features

- Complete CRUD operations for planes, cars, and ships
- Maintenance tracking and scheduling
- Status management for all vehicles
- Specific attributes and queries for each vehicle type
- RESTful API design following best practices

## Technology Stack

- Kotlin
- Spring Boot
- Spring Data JPA
- H2 Database (configurable for other databases)

## Prerequisites

- JDK 17 or higher
- Gradle 7.0 or higher
- Maven 3.6 or higher

## Getting Started

1. Clone the repository:
```bash
git clone <repository-url>
cd transportation-api
```

2. Add the following dependencies to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.h2database:h2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}
```

3. Configure your database in `application.properties`:
```properties
spring.datasource.url=jdbc:h2:mem:transportdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

## API Endpoints

### Generic Transportation Endpoints

- `GET /api/transportation` - Get all transportation
- `GET /api/transportation/{id}` - Get transportation by ID
- `GET /api/transportation/status/{status}` - Get transportation by status

### Plane Endpoints

- `GET /api/transportation/planes` - Get all planes
- `GET /api/transportation/planes/{id}` - Get plane by ID
- `GET /api/transportation/planes/status/{status}` - Get planes by status
- `GET /api/transportation/planes/range/{minRange}` - Get planes by minimum flight range
- `POST /api/transportation/planes` - Create new plane
- `PUT /api/transportation/planes/{id}` - Update plane
- `DELETE /api/transportation/planes/{id}` - Delete plane

### Car Endpoints

- `GET /api/transportation/cars` - Get all cars
- `GET /api/transportation/cars/{id}` - Get car by ID
- `GET /api/transportation/cars/status/{status}` - Get cars by status
- `GET /api/transportation/cars/fuel/{fuelType}` - Get cars by fuel type
- `GET /api/transportation/cars/transmission/{transmissionType}` - Get cars by transmission type
- `POST /api/transportation/cars` - Create new car
- `PUT /api/transportation/cars/{id}` - Update car
- `DELETE /api/transportation/cars/{id}` - Delete car

### Ship Endpoints

- `GET /api/transportation/ships` - Get all ships
- `GET /api/transportation/ships/{id}` - Get ship by ID
- `GET /api/transportation/ships/status/{status}` - Get ships by status
- `GET /api/transportation/ships/speed/{minSpeed}` - Get ships by minimum speed
- `POST /api/transportation/ships` - Create new ship
- `PUT /api/transportation/ships/{id}` - Update ship
- `DELETE /api/transportation/ships/{id}` - Delete ship

### Maintenance Endpoint

- `PUT /api/transportation/{id}/maintenance?status={status}` - Update maintenance status

## Data Models

### Transportation (Base Class)
- id: Long
- name: String
- capacity: Int
- status: TransportationStatus
- manufacturer: String
- model: String
- yearOfManufacture: Int
- lastMaintenanceDate: LocalDateTime
- nextMaintenanceDate: LocalDateTime

### Plane (extends Transportation)
- flightRange: Int (in kilometers)
- maxAltitude: Int (in meters)
- fuelCapacity: Int (in liters)

### Car (extends Transportation)
- fuelType: FuelType
- transmissionType: TransmissionType
- mileage: Int (in kilometers)

### Ship (extends Transportation)
- length: Double (in meters)
- beam: Double (in meters)
- draft: Double (in meters)
- maxSpeed: Int (in knots)

## Enums

### TransportationStatus
- AVAILABLE
- IN_USE
- MAINTENANCE
- OUT_OF_SERVICE

### FuelType
- PETROL
- DIESEL
- ELECTRIC
- HYBRID

### TransmissionType
- MANUAL
- AUTOMATIC

## Example Usage

### Creating a new plane
```bash
curl -X POST http://localhost:8080/api/transportation/planes \
-H "Content-Type: application/json" \
-d '{
    "name": "Boeing 737",
    "capacity": 150,
    "status": "AVAILABLE",
    "manufacturer": "Boeing",
    "model": "737-800",
    "yearOfManufacture": 2020,
    "lastMaintenanceDate": "2024-01-01T00:00:00",
    "nextMaintenanceDate": "2024-07-01T00:00:00",
    "flightRange": 5000,
    "maxAltitude": 12000,
    "fuelCapacity": 25000
}'
```

### Updating maintenance status
```bash
curl -X PUT "http://localhost:8080/api/transportation/1/maintenance?status=MAINTENANCE"
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 