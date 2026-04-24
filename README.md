# Pokedex REST API

A simple Spring Boot REST API that provides Pokémon information and “fun” translated descriptions using external public APIs.

---

## Features

The API exposes two main endpoints:

### 1. Get basic Pokémon information

GET /pokemon/{name}

Returns:
- Pokémon name
- Description (in English)
- Habitat
- Legendary status

---

### 2. Get translated Pokémon description

GET /pokemon/translated/{name}

Returns:
- Pokémon name
- Description (in English)
- Habitat
- Legendary status

Translation rules:
- If Pokémon is **legendary** OR habitat is **cave** → **Yoda translation**
- Otherwise → **Shakespeare translation**
- If translation fails → fallback to original description

---

## External APIs used

- PokéAPI  
  https://pokeapi.co/

- FunTranslations API  
  https://api.funtranslations.mercxry.me/

---

## Tech Stack

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring RestClient
- JUnit 5 + Mockito
- Docker

---

## Running the application locally

### Prerequisites

- Java 17
- Maven

### Steps

- mvn clean install
- mvn spring-boot:run

Application will start on:

http://localhost:8080

---

### Example requests

curl http://localhost:8080/pokemon/pikachu

curl http://localhost:8080/pokemon/translated/mewtwo

---

## Configuration

The application uses environment-based configuration.

### Default (`application.properties`)

- clients.pokeapi.base-url: https://pokeapi.co/api/v2
- clients.translations.base-url: https://api.funtranslations.mercxry.me

Env vars: POKEAPI_BASE_URL POKEAPI_SPECIES_PATH TRANSLATIONS_BASE_URL TRANSLATIONS_YODA_PATH TRANSLATIONS_SHAKESPEARE_PATH

Environment variables (for production)

- POKEAPI_BASE_URL=https://pokeapi.co/api/v2
- POKEAPI_SPECIES_PATH=/pokemon-species/{name}
- TRANSLATIONS_BASE_URL=https://api.funtranslations.mercxry.me
- TRANSLATIONS_YODA_PATH=/v1/translate/yoda
- TRANSLATIONS_SHAKESPEARE_PATH=/v1/translate/shakespeare

Used in config as:

- clients.pokeapi.base-url=${POKEAPI_BASE_URL}
- clients.pokeapi.species-path=${POKEAPI_SPECIES_PATH}
- clients.translations.base-url=${TRANSLATIONS_BASE_URL}
- clients.translations.yoda-path=${TRANSLATIONS_YODA_PATH}
- clients.translations.shakespeare-path=${TRANSLATIONS_SHAKESPEARE_PATH}

Profiles
- local → default values
- prod → environment-driven configuration

Run with profile:

mvn spring-boot:run -Dspring-boot.run.profiles=local

or

java -jar app.jar --spring.profiles.active=prod

## Docker

### Build image

docker build -t pokedex .

### Run container

docker run -p 8080:8080 pokedex

### Run with profile and env variables

docker run -p 8080:8080 \
-e SPRING_PROFILES_ACTIVE=prod \
-e POKEAPI_BASE_URL=https://pokeapi.co/api/v2 \
-e POKEAPI_SPECIES_PATH=/pokemon-species/{name} \
-e TRANSLATIONS_BASE_URL=https://api.funtranslations.mercxry.me \
-e TRANSLATIONS_YODA_PATH=/v1/translate/yoda \
-e TRANSLATIONS_SHAKESPEARE_PATH=/v1/translate/shakespeare \
pokedex

## Running tests

mvn test

## Testing Strategy

The project includes:

### Unit Tests
- PokemonServiceImpl
- Business logic (translation rules, fallback logic)
### Integration Tests
- PokemonApiClientImpl
- FunTranslationsClientImpl
- HTTP layer mocked with MockRestServiceServer
### Web Layer Tests
- PokemonController
- Tested using MockMvc
