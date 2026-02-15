package app.mediatracker.feature.integration;

import app.mediatracker.feature.search.client.anime.JikanAnimeClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the search functionality.
 * Uses application.yml from src/test/resources (Embedded Mongo + search.anime.enabled=true).
 */
@SpringBootTest
@AutoConfigureMockMvc
class SearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * We mock the external client to avoid making actual HTTP requests to the Jikan API
     * during the integration test.
     */
    @MockBean
    private JikanAnimeClient jikanAnimeClient;

    @Test
    void searchAnime_returnsResults() throws Exception {
        // given: Prepare a mock response for the external Anime API
        when(jikanAnimeClient.searchAnime("naruto"))
                .thenReturn("""
                {
                  "data": [
                    { "mal_id": 1, "title": "Naruto" }
                  ]
                }
            """);

        // when + then: Execute search and verify that the provider logic correctly maps the result
        mockMvc.perform(get("/api/search")
                        .param("q", "naruto"))
                .andExpect(status().isOk())
                // Verify provider mapping logic: e.g., mal_id becomes id
                .andExpect(jsonPath("$[0].title").value("Naruto"))
                .andExpect(jsonPath("$[0].type").value("anime"));
    }
}