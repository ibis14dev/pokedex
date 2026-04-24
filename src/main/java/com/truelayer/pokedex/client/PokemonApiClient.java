package com.truelayer.pokedex.client;

import com.truelayer.pokedex.dto.external.PokemonSpeciesResponse;

public interface PokemonApiClient {
    PokemonSpeciesResponse getPokemonSpecies(String pokemonName);
}
