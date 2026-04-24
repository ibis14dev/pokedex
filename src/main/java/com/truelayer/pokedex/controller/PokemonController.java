package com.truelayer.pokedex.controller;

import com.truelayer.pokedex.dto.PokemonResponse;
import com.truelayer.pokedex.service.PokemonService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{name}")
    public PokemonResponse getPokemonByName(@PathVariable("name") String name) {
        return pokemonService.getPokemonByName(name);
    }

    @GetMapping("/translated/{name}")
    public PokemonResponse getTranslatedPokemon(@PathVariable String name) {
        return pokemonService.getTranslatedPokemon(name);
    }
}
