# Calendar Availability Backend

Minimal Spring Boot backend for a calendar and availability interview exercise.

This project supports:

- Creating events
- Listing events that overlap a given time range
- Querying available time slots
- Preventing overlapping `APPOINTMENT` events for the same owner

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- MySQL
- Maven Wrapper
- Springdoc OpenAPI / Swagger UI

## Prerequisites

- Java 21 installed
- MySQL running locally or remotely
- Git Bash, PowerShell, or any terminal that can run Maven wrapper commands

## Environment Setup

The application loads environment variables from a local `.env` file.

1. Copy `.env.example` to `.env`
2. Fill in your database settings

Example:

```env
PORT=3000
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=123456
DB_DATABASE=masterbranch_test_db
```

## Install and Run

Run the application in development mode:

```bash
./mvnw spring-boot:run
```

On Windows Command Prompt or PowerShell:

```powershell
mvnw.cmd spring-boot:run
```

By default, the app uses the port from `.env`. If `PORT=3000`, the server will run at:

```text
http://localhost:3000
```

## Run Tests

Run all tests:

```bash
./mvnw test
```

Run specific test classes:

```bash
./mvnw -Dtest=EventServiceTest,AvailabilityServiceTest test
```

## API Documentation

Swagger UI:

```text
http://localhost:3000/api/swagger
```

OpenAPI JSON:

```text
http://localhost:3000/api/api-docs
```

## Base URL

The project currently uses:

```text
/api/api/v1
```

This is the effective base path because the application has:

- servlet context path: `/api`
- controller prefix: `/api/v1/...`

So the full runtime endpoints are under `/api/api/v1/...`.

## API Overview

All endpoints return a wrapper in this format:

```json
{
  "statusCode": 200,
  "message": "...",
  "data": {}
}
```

### 1. Create Event

`POST /api/api/v1/events`

Creates a new event.

Business rule:

- `APPOINTMENT` events for the same `ownerId` must not overlap
- If they overlap, the API returns `409 Conflict`

Request body:

```json
{
  "title": "Interview with candidate",
  "startAt": "2026-03-12T10:00:00Z",
  "endAt": "2026-03-12T11:00:00Z",
  "timezone": "Asia/Ho_Chi_Minh",
  "type": "APPOINTMENT",
  "ownerId": 1,
  "notes": "Technical interview",
  "location": "Meeting Room A",
  "attendees": ["alice@example.com", "bob@example.com"]
}
```

Success response:

```json
{
  "statusCode": 201,
  "message": "Event created successfully",
  "data": {
    "id": 1,
    "title": "Interview with candidate",
    "startAt": "2026-03-12T17:00:00.000+07:00",
    "endAt": "2026-03-12T18:00:00.000+07:00",
    "timezone": "Asia/Ho_Chi_Minh",
    "type": "APPOINTMENT",
    "ownerId": 1,
    "notes": "Technical interview",
    "location": "Meeting Room A",
    "attendees": ["alice@example.com", "bob@example.com"]
  }
}
```

### 2. List Events by Range

`GET /api/api/v1/events?ownerId=1&from=2026-03-12T08:00:00Z&to=2026-03-12T13:00:00Z`

Returns events that overlap the provided time range for a given owner.

Success response:

```json
{
  "statusCode": 200,
  "message": "Events fetched successfully",
  "data": [
    {
      "id": 1,
      "title": "Interview with candidate",
      "startAt": "2026-03-12T17:00:00.000+07:00",
      "endAt": "2026-03-12T18:00:00.000+07:00",
      "timezone": "Asia/Ho_Chi_Minh",
      "type": "APPOINTMENT",
      "ownerId": 1,
      "notes": "Technical interview",
      "location": "Meeting Room A",
      "attendees": ["alice@example.com", "bob@example.com"]
    }
  ]
}
```

### 3. Query Availability

`POST /api/api/v1/availability/query`

Returns available slots based on:

- owner
- query range
- working hours
- slot duration
- existing events

Request body:

```json
{
  "ownerId": 1,
  "from": "2026-03-12T00:00:00Z",
  "to": "2026-03-12T23:59:59Z",
  "timezone": "Asia/Ho_Chi_Minh",
  "slotDuration": 30,
  "workingHours": {
    "start": "09:00",
    "end": "17:00"
  }
}
```

Success response:

```json
{
  "statusCode": 200,
  "message": "Available slots retrieved",
  "data": {
    "ownerId": 1,
    "timezone": "Asia/Ho_Chi_Minh",
    "slots": [
      {
        "start": "2026-03-12T09:00:00.000+07:00",
        "end": "2026-03-12T09:30:00.000+07:00"
      },
      {
        "start": "2026-03-12T09:30:00.000+07:00",
        "end": "2026-03-12T10:00:00.000+07:00"
      }
    ]
  }
}
```

## Event Model

Main event fields:

- `id`: unique event identifier
- `title`: event title
- `startAt`: ISO-8601 datetime
- `endAt`: ISO-8601 datetime
- `timezone`: IANA timezone such as `Asia/Ho_Chi_Minh`
- `type`: `APPOINTMENT` or `BLOCK`
- `ownerId`: calendar owner identifier
- `notes`: optional notes
- `location`: optional location
- `attendees`: optional attendee list

## Validation Rules

- `title`, `startAt`, `endAt`, `timezone`, `type`, and `ownerId` are required when creating an event
- `endAt` must be greater than `startAt`
- `slotDuration` must be between `1` and `480`
- `workingHours.end` must be greater than `workingHours.start`
- availability query `to` must be greater than `from`

## Error Handling

Common responses:

- `400 Bad Request` for invalid input or invalid date ranges
- `409 Conflict` for overlapping `APPOINTMENT` events
- `500 Internal Server Error` for unexpected runtime errors

Example conflict response:

```json
{
  "statusCode": 409,
  "message": "Appointment overlaps with an existing appointment",
  "data": null
}
```

## Notes

- The project includes unit tests for validation, overlap handling, range filtering, and availability calculation
- The application uses MySQL and JPA with automatic schema update enabled
- The current route structure can be simplified later if you want the effective base path to become `/api/v1` instead of `/api/api/v1`
