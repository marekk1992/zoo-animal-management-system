# Zoo Animal Management System

## 1. About
This is REST API for managing animals transfer to the Zoo.
Application provides functionality of storing given enclosures from JSON file into database.

## 2. How to run this API
Before running this API, I assume that you have successfully installed the following tools on your computer:
- Git - for cloning repository from GitHub;
- Maven - for building project;
- JDK - required for Maven compilation;
- Docker - for deploying application;
- Docker-compose - for running multi-container Docker applications;
- API client tool (e.g. Postman) - for sending JSON file and testing REST API.


`Step 2` - build docker image:

Navigate to root of the project and execute command:

    $ mvn spring-boot:build-image

`Step 3` - start your project`s services:

    $ docker-compose up -d

This will start PostgreSQL and application services in the background.

`*Notes`:

1 - This REST API is test covered. You could run tests by executing command:

    $ mvn clean test

## 3. Technologies and Frameworks

- SpringBoot;
- Spring Data Rest;
- PostgreSQL;
- Docker-compose;
- Mockito;
- JUnit;
- Flyway;
- Testcontainers;