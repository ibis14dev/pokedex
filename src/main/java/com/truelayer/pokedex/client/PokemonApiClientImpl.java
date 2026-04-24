package com.truelayer.pokedex.client;

import com.truelayer.pokedex.dto.external.PokemonSpeciesResponse;
import com.truelayer.pokedex.exeption.ExternalServiceException;
import com.truelayer.pokedex.exeption.PokemonNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class PokemonApiClientImpl implements PokemonApiClient {

    private final RestClient pokeApiRestClient;

    @Value("${clients.pokeapi.species-path}")
    private String speciesPath;

    public PokemonApiClientImpl(@Qualifier("pokeApiRestClient") RestClient pokeApiRestClient) {
        this.pokeApiRestClient = pokeApiRestClient;
    }

    @Override
    public PokemonSpeciesResponse getPokemonSpecies(String pokemonName) {
        try {
            PokemonSpeciesResponse response = pokeApiRestClient.get()
                    .uri(speciesPath, pokemonName.toLowerCase())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                        if (clientResponse.getStatusCode().value() == 404) {
                            throw new PokemonNotFoundException("Pokemon not found: " + pokemonName);
                        }
                        throw new ExternalServiceException(
                                "Client error while calling PokéAPI for pokemon: " + pokemonName
                        );
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, clientResponse) -> {
                        throw new ExternalServiceException(
                                "PokéAPI server error for pokemon: " + pokemonName
                        );
                    })
                    .body(PokemonSpeciesResponse.class);

            if (response == null) {
                throw new ExternalServiceException(
                        "Empty response received from PokéAPI for pokemon: " + pokemonName
                );
            }

            return response;

        } catch (PokemonNotFoundException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            throw new ExternalServiceException(
                    "HTTP error while calling PokéAPI: status=%s, response=%s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()),
                    ex
            );
        } catch (Exception ex) {
            throw new ExternalServiceException(
                    "Unexpected error while calling PokéAPI for pokemon: " + pokemonName,
                    ex
            );
        }
    }
}