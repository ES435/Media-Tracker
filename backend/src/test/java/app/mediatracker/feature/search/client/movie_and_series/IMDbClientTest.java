package app.mediatracker.feature.search.client.movie_and_series;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for IMDbClient.
 * This test verifies that the client correctly interacts with the reactive WebClient
 * to fetch movie and series data from the IMDb API.
 */
public class IMDbClientTest {

    @Test
    void searchMovie_returnsJsonResponse() {
        // Arrange: Prepare the mock JSON response representing a movie/series search result
        String jsonResponse = """
            {
              "data": [
                { "title": "The Boys" }
              ]
            }
            """;

        // Mock the internal ExchangeFunction to simulate the HTTP response lifecycle
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(org.springframework.http.HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build()));

        // Create a WebClient.Builder using the mocked exchange function
        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);

        // Instantiate the IMDbClient with the mocked builder and the base API URL
        IMDbClient client = new IMDbClient(builder, "https://api.imdbapi.dev");

        // Act: Call the method under test
        String result = client.searchMovieAndSeries("the boys");

        // Assert: Verify that the returned JSON matches our mock and that the request was triggered
        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}