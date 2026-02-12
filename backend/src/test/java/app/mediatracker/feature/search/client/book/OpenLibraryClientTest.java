package app.mediatracker.feature.search.client.book;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OpenLibraryClientTest {

    @Test
    void searchBook_returnsJsonResponse() {
        String jsonResponse = """
            {
              "data": [
                { "title": "Hamlet" }
              ]
            }
            """;

        ExchangeFunction exchangeFunction = mock(ExchangeFunction.class);
        when(exchangeFunction.exchange(any()))
                .thenReturn(Mono.just(ClientResponse.create(org.springframework.http.HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build()));

        WebClient.Builder builder = WebClient.builder().exchangeFunction(exchangeFunction);

        OpenLibraryClient client = new OpenLibraryClient(builder, "https://openlibrary.org");

        String result = client.searchBook("hamlet", 1);

        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}
