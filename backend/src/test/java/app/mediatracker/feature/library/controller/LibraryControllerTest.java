package app.mediatracker.feature.library.controller;

import app.mediatracker.feature.auth.service.TokenService;
import app.mediatracker.feature.library.dto.AddLibraryEntryRequest;
import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.ManualEntryRequest;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import app.mediatracker.feature.search.core.dto.SearchResult;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller test for Library operations.
 * Uses WebMvcTest to isolate the web layer and AutoConfigureMockMvc to disable security filters.
 */
@WebMvcTest(LibraryController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable Security Filters for this test suite
class LibraryControllerTest {

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

    private ObjectId testUserId;
    private Principal testPrincipal;

    @BeforeEach
    void setUp() {
        testUserId = new ObjectId();
        // The Principal Name MUST be the Hex string of the ObjectId to match our JwtFilter logic!
        String userIdString = testUserId.toHexString();
        testPrincipal = () -> userIdString;

        // Ensure that when the controller calls getUserById(new ObjectId(...)),
        // Mockito correctly matches the ID.
        User mockUser = User.builder().id(testUserId).username("test-user").build();
        when(userService.getUserById(eq(testUserId))).thenReturn(mockUser);
    }

    private SearchResult validSearchResult() {
        return SearchResult.builder()
                .type("BOOK")
                .id("123")
                .title("Test Title")
                .imageUrl("http://example.com/image.jpg")
                .sourceUrl("http://example.com/source")
                .meta(Map.of("year", 2023))
                .build();
    }

    private AddLibraryEntryRequest validAddRequest() {
        AddLibraryEntryRequest request = new AddLibraryEntryRequest();
        request.setStatus(LibraryEntryStatus.PLANNED);
        request.setSearchResult(validSearchResult());
        request.setRating(7);
        request.setNotes("Some notes");
        return request;
    }

    private ManualEntryRequest validManualRequest() {
        ManualEntryRequest request = new ManualEntryRequest();
        request.setTitle("Manual Test");
        request.setType("MOVIE");
        request.setStatus(LibraryEntryStatus.COMPLETED);
        request.setRating(9);
        request.setNotes("Manual notes");
        request.setAuthor("Author Name");
        request.setImageUrl("http://example.com/movie.jpg");
        return request;
    }

    @Test
    void getLibrary_shouldReturnOkAndList() throws Exception {
        // Arrange
        when(libraryService.getLibraryForUser(eq(testUserId)))
                .thenReturn(List.of(new LibraryEntryResponse()));

        // Act & Assert
        mockMvc.perform(get("/api/library").principal(testPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(libraryService).getLibraryForUser(eq(testUserId));
    }

    @Test
    void addOrUpdateEntry_validRequest_shouldReturnOk() throws Exception {
        // Arrange
        AddLibraryEntryRequest request = validAddRequest();
        when(libraryService.addOrUpdateEntryFromSearchResult(any(), any(), any(), any(), any()))
                .thenReturn(new LibraryEntryResponse());

        // Act & Assert
        mockMvc.perform(post("/api/library")
                        .principal(testPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void addManualEntry_validRequest_shouldReturnOk() throws Exception {
        // Arrange
        ManualEntryRequest request = validManualRequest();
        when(libraryService.addManualEntry(any(ManualEntryCommand.class)))
                .thenReturn(new LibraryEntryResponse());

        // Act & Assert
        mockMvc.perform(post("/api/library/manualEntry")
                        .principal(testPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateManualEntry_shouldReturnOk() throws Exception {
        // Arrange
        ManualEntryRequest request = validManualRequest();
        when(libraryService.updateManualEntry(any(), any()))
                .thenReturn(new LibraryEntryResponse());

        // Act & Assert
        mockMvc.perform(patch("/api/library/manualEntry/{id}", "123")
                        .principal(testPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void removeEntry_shouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/library/{id}", "123").principal(testPrincipal))
                .andExpect(status().isNoContent());

        verify(libraryService).removeEntry(eq(testUserId), eq("123"));
    }
}