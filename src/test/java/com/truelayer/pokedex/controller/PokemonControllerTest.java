package com.truelayer.pokedex.controller;

import com.truelayer.pokedex.dto.PokemonResponse;
import com.truelayer.pokedex.service.PokemonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PokemonController.class)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PokemonService pokemonService;

    @Test
    void shouldReturnPokemonByName() throws Exception {
        when(pokemonService.getPokemonByName("pikachu"))
                .thenReturn(new PokemonResponse("pikachu", "Electric mouse", "forest", false));

        mockMvc.perform(get("/pokemon/pikachu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("pikachu"))
                .andExpect(jsonPath("$.description").value("Electric mouse"))
                .andExpect(jsonPath("$.habitat").value("forest"))
                .andExpect(jsonPath("$.isLegendary").value(false));
    }

    @Test
    void shouldReturnTranslatedPokemon() throws Exception {
        when(pokemonService.getTranslatedPokemon("mewtwo"))
                .thenReturn(new PokemonResponse("mewtwo", "Translated description", "rare", true));

        mockMvc.perform(get("/pokemon/translated/mewtwo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("mewtwo"))
                .andExpect(jsonPath("$.description").value("Translated description"))
                .andExpect(jsonPath("$.habitat").value("rare"))
                .andExpect(jsonPath("$.isLegendary").value(true));
    }
}
