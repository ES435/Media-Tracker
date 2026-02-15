package app.mediatracker.feature.search.client.game;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for RawgClient.
 * This test verifies that the client correctly constructs the request to the RAWG API
 * and handles the reactive stream to return the raw JSON response.
 */
public class RawgClientTest {

    @Test
    void searchGame_returnsJsonResponse() {
        // Arrange: Prepare mock JSON data representing a game search result
        String jsonResponse = """
            {
              "data": [
                { "title": "Red Dead Redemption 2" }
              ]
            }
            """;

        // Mock the ExchangeFunction to intercept the HTTP call at the lowest level of the WebClient
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);

        // Define the behavior to return a 200 OK response containing our mock JSON
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(org.springframework.http.HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build()));

        // Inject the mocked engine into a WebClient builder
        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);

        // Instantiate the client with the mocked builder and fake API credentials
        RawgClient client = new RawgClient(builder, "https://rawg.io/api", "fake-api-key");

        // Act: Execute the search (note: the typo in the search term "redemtion" won't affect the mock)
        String result = client.searchGame("red dead redemtion 2");

        // Assert: Verify that the response matches our expectation and the network call was triggered once
        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}