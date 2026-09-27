# Quiz
App that displays patient information based on user permissions

## Installation
### Install Docker
- On Windows and Mac
```
Install Docker Desktop
Windows: https://docs.docker.com/desktop/install/windows-install/
Mac: https://docs.docker.com/desktop/install/mac-install/
```
- On Linux
```bash
# For Debian-based systems
sudo apt install -y docker.io
sudo apt install docker-compose

# For Arch-based systems
sudo pacman -S docker.io
sudo pacman -S docker-compose

# Making Docker run now and on startup
sudo systemctl start docker
sudo systemctl enable docker

# Using the CLI without sudo
sudo usermod -aG docker $USER
sudo reboot
```

## How to run
- Navigate to the root of the project
- Open a terminal
- Start the containers:
```bash
docker compose up
```
- Visit localhost:8080/users to add a new user and set the permissions, and then navigate to localhost:8080 to access the patient data

## Local Development
- You need containers for PostgreSQL and HAPI FHIR. For this program they are called `my-postgres` and `hapi-fhir-service`
  - If you wish to name them differently, update the `application.properties` and `FhirConfig`
  - For the images and their versions, check the `docker-compose.yml`
- Ensure both are on the `shared-db-network`
  - If you want to name the network differently, make sure to update the `devcontainer.json`
