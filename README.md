# Video Processing API

This project is a Spring Boot application that provides various video processing features such as uploading, trimming, merging, and sharing videos with time-based expiry links.

## Table of Contents

1. [Features](#features)
2. [Technology Stack](#technology-stack)
3. [Design Decisions](#design-decisions)
4. [Setup Instructions](#setup-instructions)
5. [Running the Application](#running-the-application)
6. [API Endpoints](#api-endpoints)
7. [Authentication](#authentication)
8. [Postman Collection](#postman-collection)
9. [Contributing](#contributing)
   

## Features

- **Video Upload**: Upload videos with configurable size limits.
- **Video Merging**: Merge multiple video clips into one file.
- **Video Trimming**: Trim videos based on start and end time.
- **Link Sharing**: Generate a shareable link with expiry for a video file.
- **Download**: Download the video using the shared link.

## Technology Stack

- **Java 21**: The application is built using Java 21.
- **Spring Boot 3.3.3**: The core framework used for building the RESTful API.
- **Spring Data JPA**: For database interactions using the repository pattern.
- **Hibernate ORM**: For Object-Relational Mapping (ORM) with support for SQLite through custom dialects.
- **SQLite**: Database for storing video metadata and shared links.
- **FFmpeg (6.1.1)**: Library used for video processing tasks such as trimming and merging.
- **Thymeleaf**: Template engine used for rendering dynamic content (though currently not utilized in controllers).
- **Maven**: Dependency management and build tool.
- **Lombok**: To reduce boilerplate code like getters, setters, and constructors (optional).
- **JUnit 5 & Mockito**: For writing unit and integration tests.
# Design Decisions for Video Management System

### 1. Architecture
- **Spring Boot Framework**: Chosen for its ease of use and rapid development capabilities, allowing for RESTful API creation and integration with other components.
- **Microservices Approach**: Enables better scalability and maintainability, allowing different features to evolve independently.

### 2. Database
- **SQLite**: Selected for its lightweight nature and ease of setup. It’s suitable for development and small-scale applications, ensuring quick deployment without additional configuration.

### 3. Video Processing
- **FFmpeg Integration**: Utilized for video manipulation tasks (uploading, trimming, merging). FFmpeg is a robust library that handles a wide range of video formats and operations efficiently.

### 4. API Authentication
- **Static API Tokens**: Implemented for authentication to ensure that all API calls are secure. This simplifies the authentication mechanism for quick implementation while maintaining basic security.

### 5. File Handling
- **Video Uploads**: Configurable limits for size and duration to ensure that the system handles video files effectively without overwhelming resources.

### 6. Testing
- **Unit and E2E Tests**: Committed to testing the functionality of API endpoints and video processing methods, ensuring reliability and robustness of the system.

### 7. Documentation
- **Swagger & Postman**: Used for API documentation to provide a clear and interactive way for users and developers to understand and test the API endpoints.

## Setup Instructions

1. **Clone the repository**:
``` bash
git clone https://github.com/your-repo/video-processing-api.git
```
2. Install ffmpeg - command line utility and libraries for video processing 
visit - https://ffmpeg.org/download.html
run command to verify ffmpeg has been install and added to the env variables.
``` bash
ffmpeg -version
```
3. Install dependencies: Make sure you have Maven installed, then run:
``` bash
./mvnw clean install
```
4. Configure FFmpeg: Ensure FFmpeg is installed on your machine and is available in your system's PATH.
5. Configure Database: SQLite is pre-configured. The database will be created automatically when the application starts.

## Running the Application
### Run the test 
``` bash
./mvnw test
```
### Using Maven: 
``` bash
./mvnw spring-boot:run
```

### Authentication
* All API calls must include an API token in the headers. A static API token is assumed to authenticate the user.
#### Initially clients will have to add Header for every request they want to execute 
#### Header will consist of Key : `Authorization` , Value : `Token`
## API Endpoints
1. Video Upload
  `POST /api/videos/upload`
  **Description**: Upload a video file.
  **Request Parameters**:
  file: The video file to be uploaded (MultipartFile).
  **Response**: A success message and video metadata.

2. Video Download
`GET /api/videos/{videoId}`
**Description**: Download the video by ID.
**Response**: The video file in MP4 format

3. Share Video Link
`POST /share/{videoId}`
**Description**: Generate a shareable link for a video, valid for 24 hours.
**Response**: The shareable download link.

4. Download Video via Shared Link
`GET /download/{shareToken}`
**Description**: Download a video using the provided share token.
**Response**: The video file.

5. Trim Video
`GET /api/edit/{videoId}/trim/{startTime}/{endTime}`
**Description**: Trim the video between specified start and end times.
**Response**: The trimmed video.

6. Merge Videos
`POST /api/edit/merge`
**Description**: Merge multiple video clips into a single file.
**Request Parameters**:
clips: List of video clips (MultipartFile).
**Response**: The merged video

## Postman Collection
* For easier testing, a Postman collection is available that includes all endpoints.
* To Import: Download the Postman collection from Postman Collection.
Open Postman.
Click on Import.
Select the downloaded collection JSON file.
### The collection will now be available in ./main/resources/Video Editor API.postman_collection.json

### Contributing
If you'd like to contribute to the project, feel free to fork the repository and submit a pull request.
