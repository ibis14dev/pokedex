package com.truelayer.pokedex.service;

import com.truelayer.pokedex.dto.PokemonResponse;

public interface PokemonService {
    public PokemonResponse getPokemonByName(String name);

    public PokemonResponse getTranslatedPokemon(String name);

}
