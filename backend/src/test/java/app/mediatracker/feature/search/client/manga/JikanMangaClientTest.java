package app.mediatracker.feature.search.client.manga;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JikanMangaClientTest {

    @Test
    void searchManga_returnsJsonResponse () {
        String jsonResponse = """
            {
              "data": [
                { "title": "The Summer You Were There" }
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

        JikanMangaClient client = new JikanMangaClient(builder, "https://api.jikan.moe/v4");

        String result = client.searchManga("the summer you were there");

        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}
