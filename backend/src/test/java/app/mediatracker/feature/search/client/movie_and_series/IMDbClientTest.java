package app.mediatracker.feature.search.client.movie_and_series;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IMDbClientTest {

    @Test
    void searchMovie_returnsJsonResponse() {

        String jsonResponse = """
            {
              "data": [
                { "title": "The Boys" }
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

        IMDbClient client = new IMDbClient(builder,"https://api.imdbapi.dev");

        String result = client.searchMovieAndSeries("the boys");

        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}
