# Zoo Animal Management System

## 1. About
This is REST API for managing animals transfer to the Zoo.
Application stores given enclosures and animals from JSON files into database, while also assigning suitable enclosure for each animal.

The animals are housed according to a set of rules, some of which may seem illogical and unrealistic in a real world:
1. The size of an enclosure is measured in terms of how many animals it can accommodate, not its territorial size, e.g. "small" enclosure could store 3 animals, "medium" - 7, etc. That way elephant may end up in small enclosure;
2. Animals placed in enclosures without paying attention to its environment. Wolf may end up in enclosure with climbing structures, trees, which may be more suitable for Gorillas/Pandas;
3. Meat-eating animals of different species can be placed in same enclosure only in if there are no enclosures with them inside and only two different species of meat-eating animals can be grouped together;
4. Vegetarian animals can be placed together in same enclosure;
5. Vegetarian animals can be placed together with meat-eating animals in same enclosure; 
6. Animals of same species can`t be separated.

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