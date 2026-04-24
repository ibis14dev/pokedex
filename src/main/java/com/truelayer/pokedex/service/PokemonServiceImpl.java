package com.truelayer.pokedex.service;

import com.truelayer.pokedex.client.FunTranslationsClient;
import com.truelayer.pokedex.client.PokemonApiClient;
import com.truelayer.pokedex.dto.PokemonResponse;
import com.truelayer.pokedex.dto.external.PokemonSpeciesResponse;
import com.truelayer.pokedex.exeption.TranslationException;
import com.truelayer.pokedex.mapper.PokemonMapper;
import com.truelayer.pokedex.model.TranslationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokemonServiceImpl implements PokemonService {

    private final PokemonApiClient pokeApiClient;
    private final PokemonMapper pokemonMapper;
    private final FunTranslationsClient funTranslationsClient;

    @Override
    public PokemonResponse getPokemonByName(String name) {
        PokemonSpeciesResponse species = pokeApiClient.getPokemonSpecies(name);
        String description = pokemonMapper.extractEnglishDescription(species);
        return pokemonMapper.toPokemonResponse(species, description);
    }

    @Override
    public PokemonResponse getTranslatedPokemon(String name) {
        PokemonSpeciesResponse species = pokeApiClient.getPokemonSpecies(name);
        String standardDescription = pokemonMapper.extractEnglishDescription(species);

        TranslationType translationType = resolveTranslationType(
                species.habitat() != null ? species.habitat().name() : null,
                species.isLegendary()
        );

        String finalDescription;
        try {
            finalDescription = funTranslationsClient.translate(standardDescription, translationType);
        } catch (TranslationException ex) {
            finalDescription = standardDescription;
        }

        return pokemonMapper.toPokemonResponse(species, finalDescription);
    }

    private TranslationType resolveTranslationType(String habitat, boolean isLegendary) {
        if (isLegendary || "cave".equalsIgnoreCase(habitat)) {
            return TranslationType.YODA;
        }
        return TranslationType.SHAKESPEARE;
    }
}
