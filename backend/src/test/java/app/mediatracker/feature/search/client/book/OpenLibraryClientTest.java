package app.mediatracker.feature.search.client.book;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test for OpenLibraryClient.
 * This test verifies the correct configuration of the WebClient request and
 * ensures the raw JSON response from the OpenLibrary API is returned correctly.
 */
public class OpenLibraryClientTest {

    @Test
    void searchBook_returnsJsonResponse() {
        // Arrange: Prepare mock data
        String jsonResponse = """
            {
              "data": [
                { "title": "Hamlet" }
              ]
            }
            """;

        // Mock the internal exchange function to simulate an HTTP response
        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(org.springframework.http.HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build()));

        // Build the client with the mocked engine
        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);
        OpenLibraryClient client = new OpenLibraryClient(builder, "https://openlibrary.org");

        // Act: Perform the book search
        String result = client.searchBook("hamlet", 1);

        // Assert: Verify results and internal interactions
        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}