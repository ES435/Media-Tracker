package app.mediatracker.feature.search.client.game;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RawgClientTest {

    @Test
    void searchGame_returnsJsonResponse() {
        String jsonResponse = """
            {
              "data": [
                { "title": "Red Dead Redemption 2" }
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

        RawgClient client = new RawgClient(builder,"https://rawg.io/api","fake-api-key");

        String result = client.searchGame("red dead redemtion 2");

        assertEquals(jsonResponse, result);
        verify(exchangeFunction, times(1)).exchange(any());
    }
}
