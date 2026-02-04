package app.mediatracker.feature.search;

import app.mediatracker.feature.search.client.anime.JikanAnimeClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class SearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
                .andExpect(jsonPath("$[0].title").value("Naruto"))
                .andExpect(jsonPath("$[0].type").value("anime"));
    }
}

