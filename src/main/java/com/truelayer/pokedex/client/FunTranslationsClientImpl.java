package com.truelayer.pokedex.client;

import com.truelayer.pokedex.dto.external.TranslationRequest;
import com.truelayer.pokedex.dto.external.TranslationSuccessResponse;
import com.truelayer.pokedex.exeption.TranslationException;
import com.truelayer.pokedex.model.TranslationType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class FunTranslationsClientImpl implements FunTranslationsClient {

    private final RestClient translationRestClient;

    public FunTranslationsClientImpl(@Qualifier("translationRestClient") RestClient translationRestClient) {
        this.translationRestClient = translationRestClient;
    }

    @Override
    public String translate(String text, TranslationType translationType) {
        String endpoint = switch (translationType) {
            case YODA -> "/v1/translate/yoda";
            case SHAKESPEARE -> "/v1/translate/shakespeare";
        };

        try {
            TranslationSuccessResponse response = translationRestClient.post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(new TranslationRequest(text))
                    .retrieve()
                    .body(TranslationSuccessResponse.class);

            if (response == null || response.contents() == null || response.contents().translated() == null) {
                throw new TranslationException("Empty translation response");
            }

            return response.contents().translated();
        } catch (RestClientResponseException ex) {
            throw new TranslationException(
                    "Translation API error: status=%s, response=%s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()),
                    ex
            );
        } catch (Exception ex) {
            throw new TranslationException(
                    "Unexpected error while translating text with " + translationType,
                    ex
            );
        }
    }
}
