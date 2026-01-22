package app.mediatracker.feature.library.controller;

import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.auth.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(app.mediatracker.feature.library.controller.LibraryController.class)
class LibraryControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getLibrary_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/library"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getLibraryPage_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/library/page"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addOrUpdateEntry_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addManualEntry_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/library/manualEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateManualEntry_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/library/manualEntry/{id}", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeEntry_withoutToken_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/library/{id}", "123"))
                .andExpect(status().isForbidden());
    }
}

