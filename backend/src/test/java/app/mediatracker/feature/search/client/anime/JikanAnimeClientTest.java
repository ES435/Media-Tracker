package app.mediatracker.feature.search.client.anime;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JikanAnimeClientTest {

    @Test
    void searchAnime_returnsJsonResponse() {
        // Arrange
        String jsonResponse = """
            {
              "data": [
                { "title": "Naruto" }
              ]
            }
            """;

        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);

        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(
                        ClientResponse.create(org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body(jsonResponse)
                                .build()
                ));

        WebClient.Builder builder = WebClient.builder()
                .exchangeFunction(exchangeFunction);

        JikanAnimeClient client =
                new JikanAnimeClient(builder, "https://api.jikan.moe/v4");

        // Act
        String result = client.searchAnime("naruto");

        // Assert
        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}

