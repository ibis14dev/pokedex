package com.truelayer.pokedex.service;

import com.truelayer.pokedex.client.FunTranslationsClient;
import com.truelayer.pokedex.client.PokemonApiClient;
import com.truelayer.pokedex.dto.PokemonResponse;
import com.truelayer.pokedex.dto.external.NamedApiResource;
import com.truelayer.pokedex.dto.external.PokemonSpeciesResponse;
import com.truelayer.pokedex.exeption.TranslationException;
import com.truelayer.pokedex.mapper.PokemonMapper;
import com.truelayer.pokedex.model.TranslationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonServiceImplTest {

    @Mock
    private PokemonApiClient pokeApiClient;

    @Mock
    private PokemonMapper pokemonMapper;

    @Mock
    private FunTranslationsClient funTranslationsClient;

    @InjectMocks
    private PokemonServiceImpl pokemonService;

    private PokemonSpeciesResponse legendarySpecies;
    private PokemonSpeciesResponse caveSpecies;
    private PokemonSpeciesResponse normalSpecies;

    @BeforeEach
    void setUp() {
        legendarySpecies = new PokemonSpeciesResponse(
                "mewtwo",
                new NamedApiResource("rare", "url"),
                true,
                List.of()
        );

        caveSpecies = new PokemonSpeciesResponse(
                "zubat",
                new NamedApiResource("cave", "url"),
                false,
                List.of()
        );

        normalSpecies = new PokemonSpeciesResponse(
                "pikachu",
                new NamedApiResource("forest", "url"),
                false,
                List.of()
        );
    }

    @Test
    void shouldReturnBasicPokemonInfo() {
        when(pokeApiClient.getPokemonSpecies("pikachu")).thenReturn(normalSpecies);
        when(pokemonMapper.extractEnglishDescription(normalSpecies)).thenReturn("Electric mouse.");
        when(pokemonMapper.toPokemonResponse(normalSpecies, "Electric mouse."))
                .thenReturn(new PokemonResponse("pikachu", "Electric mouse.", "forest", false));

        PokemonResponse response = pokemonService.getPokemonByName("pikachu");

        assertThat(response.name()).isEqualTo("pikachu");
        assertThat(response.description()).isEqualTo("Electric mouse.");
        assertThat(response.habitat()).isEqualTo("forest");
        assertThat(response.isLegendary()).isFalse();
    }

    @Test
    void shouldUseYodaTranslationWhenPokemonIsLegendary() {
        when(pokeApiClient.getPokemonSpecies("mewtwo")).thenReturn(legendarySpecies);
        when(pokemonMapper.extractEnglishDescription(legendarySpecies)).thenReturn("Standard description");
        when(funTranslationsClient.translate("Standard description", TranslationType.YODA))
                .thenReturn("Yoda description");
        when(pokemonMapper.toPokemonResponse(legendarySpecies, "Yoda description"))
                .thenReturn(new PokemonResponse("mewtwo", "Yoda description", "rare", true));

        PokemonResponse response = pokemonService.getTranslatedPokemon("mewtwo");

        assertThat(response.description()).isEqualTo("Yoda description");
        verify(funTranslationsClient).translate("Standard description", TranslationType.YODA);
    }

    @Test
    void shouldUseYodaTranslationWhenHabitatIsCave() {
        when(pokeApiClient.getPokemonSpecies("zubat")).thenReturn(caveSpecies);
        when(pokemonMapper.extractEnglishDescription(caveSpecies)).thenReturn("Bat description");
        when(funTranslationsClient.translate("Bat description", TranslationType.YODA))
                .thenReturn("Yoda bat description");
        when(pokemonMapper.toPokemonResponse(caveSpecies, "Yoda bat description"))
                .thenReturn(new PokemonResponse("zubat", "Yoda bat description", "cave", false));

        PokemonResponse response = pokemonService.getTranslatedPokemon("zubat");

        assertThat(response.description()).isEqualTo("Yoda bat description");
        verify(funTranslationsClient).translate("Bat description", TranslationType.YODA);
    }

    @Test
    void shouldUseShakespeareTranslationWhenPokemonIsNotLegendaryAndHabitatIsNotCave() {
        when(pokeApiClient.getPokemonSpecies("pikachu")).thenReturn(normalSpecies);
        when(pokemonMapper.extractEnglishDescription(normalSpecies)).thenReturn("Mouse description");
        when(funTranslationsClient.translate("Mouse description", TranslationType.SHAKESPEARE))
                .thenReturn("Shakespeare description");
        when(pokemonMapper.toPokemonResponse(normalSpecies, "Shakespeare description"))
                .thenReturn(new PokemonResponse("pikachu", "Shakespeare description", "forest", false));

        PokemonResponse response = pokemonService.getTranslatedPokemon("pikachu");

        assertThat(response.description()).isEqualTo("Shakespeare description");
        verify(funTranslationsClient).translate("Mouse description", TranslationType.SHAKESPEARE);
    }

    @Test
    void shouldFallbackToStandardDescriptionWhenTranslationFails() {
        when(pokeApiClient.getPokemonSpecies("pikachu")).thenReturn(normalSpecies);
        when(pokemonMapper.extractEnglishDescription(normalSpecies)).thenReturn("Standard description");
        when(funTranslationsClient.translate("Standard description", TranslationType.SHAKESPEARE))
                .thenThrow(new TranslationException("Translation failed"));
        when(pokemonMapper.toPokemonResponse(normalSpecies, "Standard description"))
                .thenReturn(new PokemonResponse("pikachu", "Standard description", "forest", false));

        PokemonResponse response = pokemonService.getTranslatedPokemon("pikachu");

        assertThat(response.description()).isEqualTo("Standard description");
        verify(funTranslationsClient).translate("Standard description", TranslationType.SHAKESPEARE);
    }
}