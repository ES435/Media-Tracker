package app.mediatracker.feature.search.client.music;

import app.mediatracker.feature.search.client.book.OpenLibraryClient;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ItunesClientTest {

    @Test
    void searchMusic_returnsJsonResponse() {
        String jsonResponse = """
            {
              "data": [
                { "title": "Lush Life" }
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

        ItunesClient client = new ItunesClient(builder, "https://itunes.apple.com");

        String result = client.searchTracks("lush life", 1);

        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}
