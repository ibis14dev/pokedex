package com.truelayer.pokedex.client;

import com.truelayer.pokedex.model.TranslationType;

public interface FunTranslationsClient {
    String translate(String text, TranslationType translationType);
}
