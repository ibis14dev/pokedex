package com.truelayer.pokedex.client;

import com.truelayer.pokedex.dto.external.PokemonSpeciesResponse;
import com.truelayer.pokedex.exeption.ExternalServiceException;
import com.truelayer.pokedex.exeption.PokemonNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.springframework.http.HttpMethod.GET;

class PokemonApiClientImplTest {

    private MockRestServiceServer server;
    private PokemonApiClientImpl client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();

        RestClient restClient = RestClient.builder(restTemplate)
                .baseUrl("https://pokeapi.co/api/v2")
                .build();

        client = new PokemonApiClientImpl(restClient);

        ReflectionTestUtils.setField(
                client,
                "speciesPath",
                "/pokemon-species/{name}"
        );
    }

    @Test
    void shouldReturnPokemonSpeciesWhenApiRespondsOk() {
        String body = """
                {
                  "name": "pikachu",
                  "habitat": { "name": "forest", "url": "url" },
                  "is_legendary": false,
                  "flavor_text_entries": []
                }
                """;

        server.expect(requestTo("https://pokeapi.co/api/v2/pokemon-species/pikachu"))
                .andExpect(method(GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        PokemonSpeciesResponse response = client.getPokemonSpecies("pikachu");

        assertThat(response.name()).isEqualTo("pikachu");
        assertThat(response.habitat().name()).isEqualTo("forest");
        assertThat(response.isLegendary()).isFalse();

        server.verify();
    }

    @Test
    void shouldThrowPokemonNotFoundExceptionWhenApiReturns404() {
        server.expect(requestTo("https://pokeapi.co/api/v2/pokemon-species/missingno"))
                .andExpect(method(GET))
                .andRespond(withStatus(org.springframework.http.HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.getPokemonSpecies("missingno"))
                .isInstanceOf(PokemonNotFoundException.class);

        server.verify();
    }

    @Test
    void shouldThrowExternalServiceExceptionWhenApiReturns500() {
        server.expect(requestTo("https://pokeapi.co/api/v2/pokemon-species/pikachu"))
                .andExpect(method(GET))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getPokemonSpecies("pikachu"))
                .isInstanceOf(ExternalServiceException.class);

        server.verify();
    }
}
