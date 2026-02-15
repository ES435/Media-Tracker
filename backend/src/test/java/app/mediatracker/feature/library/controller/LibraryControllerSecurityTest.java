package app.mediatracker.feature.library.controller;

import app.mediatracker.feature.auth.service.TokenService;
import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security tests for the LibraryController.
 * Ensures that protected library endpoints are not accessible without valid authentication.
 */
@WebMvcTest(app.mediatracker.feature.library.controller.LibraryController.class)
class LibraryControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getLibrary_withoutToken_shouldReturnUnauthorized() throws Exception {
        // Verifies that fetching the raw library list requires authentication
        mockMvc.perform(get("/api/library"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getLibraryPage_withoutToken_shouldReturnUnauthorized() throws Exception {
        // Verifies that fetching the UI-optimized library page requires authentication
        mockMvc.perform(get("/api/library/page"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addOrUpdateEntry_withoutToken_shouldReturnForbidden() throws Exception {
        // Verifies that adding/updating synced API entries requires authentication
        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void addManualEntry_withoutToken_shouldReturnForbidden() throws Exception {
        // Verifies that creating a new manual entry requires authentication
        mockMvc.perform(post("/api/library/manualEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateManualEntry_withoutToken_shouldReturnForbidden() throws Exception {
        // Verifies that updating an existing manual entry requires authentication
        mockMvc.perform(patch("/api/library/manualEntry/{id}", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeEntry_withoutToken_shouldReturnForbidden() throws Exception {
        // Verifies that deleting an entry requires authentication
        mockMvc.perform(delete("/api/library/{id}", "123"))
                .andExpect(status().isForbidden());
    }
}