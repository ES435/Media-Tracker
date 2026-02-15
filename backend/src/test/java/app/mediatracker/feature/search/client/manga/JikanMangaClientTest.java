package app.mediatracker.feature.search.client.manga;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for JikanMangaClient.
 * Verifies that the client correctly handles the reactive WebClient exchange
 * and returns the raw JSON response from the Jikan Manga API.
 */
public class JikanMangaClientTest {

    @Test
    void searchManga_returnsJsonResponse() {
        // Arrange: Prepare the mock JSON response
        String jsonResponse = """
            {
              "data": [
                { "title": "The Summer You Were There" }
              ]
            }
            """;

        // Mock the ExchangeFunction to intercept the low-level HTTP call
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(org.springframework.http.HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build()));

        // Inject the mocked engine into a WebClient builder
        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);

        // Instantiate the client with the mocked builder and the Jikan base URL
        JikanMangaClient client = new JikanMangaClient(builder, "https://api.jikan.moe/v4");

        // Act: Execute the manga search
        String result = client.searchManga("the summer you were there");

        // Assert: Verify the result and ensure the exchange function was called exactly once
        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}