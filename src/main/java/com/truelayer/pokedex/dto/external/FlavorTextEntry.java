package com.truelayer.pokedex.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FlavorTextEntry(
        @JsonProperty("flavor_text")
        String flavorText,
        LanguageResource language
) {}
