package com.truelayer.pokedex.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PokemonSpeciesResponse(
        String name,
        NamedApiResource habitat,
        @JsonProperty("is_legendary")
        boolean isLegendary,
        @JsonProperty("flavor_text_entries")
        List<FlavorTextEntry> flavorTextEntries
) {}
