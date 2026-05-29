GitHub Repositories Proxy API

Overview

This application is a lightweight proxy REST API built with Spring Boot, which integrates with the public GitHub API (https://developer.github.com/v3).

The service exposes a single endpoint that allows retrieving a list of non-fork GitHub repositories for a given user, along with their branches and last commit SHA.

The application is designed following a simple Controller / Service / Client architecture and acts as a data transformation layer between GitHub API and the consumer.

Features

Main functionality

For a given GitHub username, the API returns:
- repository name
- owner login
- list of branches per repository
    - branch name
    - last commit SHA

Only non-fork repositories are included in the response.

Error handling

If the requested GitHub user does not exist, the API returns:

{
"status": 404,
"message": "User not found"
}

Architecture

The project follows a simple 3-layer architecture:

Controller → Service → Client → GitHub API

Layers:
- Controller – exposes REST endpoint
- Service – contains business logic (filtering forks, mapping data)
- Client – handles communication with GitHub API using RestClient

No additional architectural patterns (DDD / Hexagonal) were used, per task requirements.

API Endpoint

Get repositories by username

GET /api/github/{username}

Example response

[
{
"repositoryName": "repo1",
"ownerLogin": "octocat",
"branches": [
{
"name": "main",
"lastCommitSha": "abc123"
}
]
}
]

Tech Stack

- Java 25
- Spring Boot 4
- Gradle (Kotlin DSL)
- Spring Web
- RestClient (Spring)
- WireMock (integration tests)
- JUnit 5

Testing

The project includes integration tests only, as required.

Approach:
- no mocks (except WireMock for external API emulation)
- real Spring context
- WireMock used to simulate GitHub API responses
- minimal number of tests focusing on business logic

Covered scenarios:
- filtering out forked repositories
- returning repository branches with commit SHA
- handling GitHub user not found (404)

Configuration

Application properties:
- github.api.base-url=https://api.github.com

Test properties:
- github.api.base-url=http://localhost:8089
- server.port=8080

Running tests

./gradlew clean test

Design decisions

The application acts as a proxy layer over GitHub API
- only two models are used
    - DTO (API response)
    - business model
- no pagination support (as per requirements)
- no security layer (as per task constraints)
- external API communication isolated in a dedicated client class
- error handling mapped to required API contract

Build & Run

Run application:
./gradlew bootRun

Build project:
./gradlew build

Author

Project created as a recruitment task demonstrating:
- Spring Boot REST API design
- external API integration
- clean architecture (lightweight)
- integration testing with WireMock
