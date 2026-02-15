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

// Nutzt application.yml aus src/test/resources (Embedded Mongo + search.anime.enabled=true)
@SpringBootTest
@AutoConfigureMockMvc
class SearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Wir mocken den externen Client, damit wir nicht wirklich Jikan anfragen
    @MockBean
    private JikanAnimeClient jikanAnimeClient;

    @Test
    void searchAnime_returnsResults() throws Exception {
        // given
        when(jikanAnimeClient.searchAnime("naruto"))
                .thenReturn("""
                {
                  "data": [
                    { "mal_id": 1, "title": "Naruto" }
                  ]
                }
            """);

        // when + then
        mockMvc.perform(get("/api/search")
                        .param("q", "naruto"))
                .andExpect(status().isOk())
                // Provider-Logik: mapped mal_id -> id
                .andExpect(jsonPath("$[0].title").value("Naruto"))
                .andExpect(jsonPath("$[0].type").value("anime"));
    }
}