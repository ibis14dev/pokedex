package com.truelayer.pokedex.dto;

public record PokemonResponse(
        String name,
        String description,
        String habitat,
        boolean isLegendary
) {}
