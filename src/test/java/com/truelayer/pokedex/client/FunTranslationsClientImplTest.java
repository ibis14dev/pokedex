package com.truelayer.pokedex.client;

import com.truelayer.pokedex.exeption.TranslationException;
import com.truelayer.pokedex.model.TranslationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class FunTranslationsClientImplTest {

    private MockRestServiceServer server;
    private FunTranslationsClientImpl client;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        RestClient restClient = RestClient.builder(restTemplate)
                .baseUrl("https://api.funtranslations.mercxry.me")
                .build();

        client = new FunTranslationsClientImpl(restClient);
    }

    @Test
    void shouldTranslateUsingYodaEndpoint() {
        String body = """
                {
                  "success": { "total": 1 },
                  "contents": {
                    "translated": "Most afraid of the dark, i am, hmm.",
                    "text": "I am very afraid of the dark.",
                    "translation": "yoda"
                  }
                }
                """;

        server.expect(requestTo("https://api.funtranslations.mercxry.me/v1/translate/yoda"))
                .andExpect(method(POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        String translated = client.translate("I am very afraid of the dark.", TranslationType.YODA);

        assertThat(translated).isEqualTo("Most afraid of the dark, i am, hmm.");

        server.verify();
    }

    @Test
    void shouldTranslateUsingShakespeareEndpoint() {
        String body = """
                {
                  "success": { "total": 1 },
                  "contents": {
                    "translated": "Verily, hello world.",
                    "text": "Hello world.",
                    "translation": "shakespeare"
                  }
                }
                """;

        server.expect(requestTo("https://api.funtranslations.mercxry.me/v1/translate/shakespeare"))
                .andExpect(method(POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        String translated = client.translate("Hello world.", TranslationType.SHAKESPEARE);

        assertThat(translated).isEqualTo("Verily, hello world.");

        server.verify();
    }

    @Test
    void shouldThrowTranslationExceptionWhenResponseIsEmpty() {
        String body = """
                {
                  "success": { "total": 1 },
                  "contents": {
                    "translated": null,
                    "text": "Hello world.",
                    "translation": "shakespeare"
                  }
                }
                """;

        server.expect(requestTo("https://api.funtranslations.mercxry.me/v1/translate/shakespeare"))
                .andExpect(method(POST))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.translate("Hello world.", TranslationType.SHAKESPEARE))
                .isInstanceOf(TranslationException.class);

        server.verify();
    }

    @Test
    void shouldThrowTranslationExceptionWhenApiReturns4xx() {
        server.expect(requestTo("https://api.funtranslations.mercxry.me/v1/translate/yoda"))
                .andExpect(method(POST))
                .andRespond(withStatus(org.springframework.http.HttpStatus.FORBIDDEN));

        assertThatThrownBy(() -> client.translate("Hello world.", TranslationType.YODA))
                .isInstanceOf(TranslationException.class);

        server.verify();
    }
}