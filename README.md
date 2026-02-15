# Media Tracker 3

This README provides a guide on how to set up and run this project. For information about the goals and features, visit the [MT3-Wiki](https://gitlab.mi.hdm-stuttgart.de/es171/media-tracker-3/-/wikis/Home).


## Installation
### Requirements
* **Java 21**: Verify your version by running `java -version`.
* **Docker & Docker Compose**: Ensure the Docker Desktop (or Engine) is running.
### Cloning the project
Run one of the following commands to create a local copy of this project:
* **HTTPS**: `git clone https://gitlab.mi.hdm-stuttgart.de/es171/media-tracker-3.git`
* **SSH**: `git clone git@gitlab.mi.hdm-stuttgart.de:es171/media-tracker-3.git` 

### Start the containers
1. Navigate to the /docker directory.
2. Run `docker compose up -d`. There should be 3 Containers running now: mt3-frontend, mt3-backend, and mt3-mongo. 

### Run the Tests
To execute the tests:
1. Navigate to /backend.
2. Run `./mvnw clean test`.
