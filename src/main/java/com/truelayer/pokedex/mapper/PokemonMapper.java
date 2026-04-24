package com.truelayer.pokedex.mapper;

import com.truelayer.pokedex.dto.PokemonResponse;
import com.truelayer.pokedex.dto.external.FlavorTextEntry;
import com.truelayer.pokedex.dto.external.PokemonSpeciesResponse;
import org.springframework.stereotype.Component;

@Component
public class PokemonMapper {

    public PokemonResponse toPokemonResponse(PokemonSpeciesResponse species, String description) {
        return new PokemonResponse(
                species.name(),
                description,
                species.habitat() != null ? species.habitat().name() : null,
                species.isLegendary()
        );
    }

    public String extractEnglishDescription(PokemonSpeciesResponse species) {
        return species.flavorTextEntries().stream()
                .filter(entry -> entry.language() != null && "en".equalsIgnoreCase(entry.language().name()))
                .map(FlavorTextEntry::flavorText)
                .map(this::sanitize)
                .findFirst()
                .orElse("");
    }

    private String sanitize(String text) {
        return text == null
                ? ""
                : text.replace("\n", " ")
                .replace("\f", " ")
                .trim();
    }

}
