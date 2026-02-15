package app.mediatracker.feature.search.client.music;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for ItunesClient.
 * This test verifies that the music search request is correctly dispatched via the WebClient
 * and that the raw JSON response from the iTunes API is handled properly.
 */
public class ItunesClientTest {

    @Test
    void searchMusic_returnsJsonResponse() {
        // Arrange: Prepare a mock JSON response representing a music track
        String jsonResponse = """
            {
              "data": [
                { "title": "Lush Life" }
              ]
            }
            """;

        // Mock the ExchangeFunction to bypass actual network calls
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);

        // Instruct the mock to return a Mono containing a successful HTTP response
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(org.springframework.http.HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build()));

        // Create a WebClient using the mocked engine
        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);

        // Instantiate the iTunes client with the mocked builder
        ItunesClient client = new ItunesClient(builder, "https://itunes.apple.com");

        // Act: Execute the search for a specific track
        String result = client.searchTracks("lush life", 1);

        // Assert: Verify the output matches our mock and that the client actually triggered a request
        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}