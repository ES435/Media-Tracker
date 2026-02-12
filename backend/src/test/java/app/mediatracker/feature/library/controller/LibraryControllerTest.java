package app.mediatracker.feature.library.controller;

import app.mediatracker.feature.auth.service.JwtService;
import app.mediatracker.feature.library.dto.AddLibraryEntryRequest;
import app.mediatracker.feature.library.dto.LibraryEntryResponse;
import app.mediatracker.feature.library.dto.ManualEntryRequest;
import app.mediatracker.feature.library.service.LibraryService;
import app.mediatracker.feature.library.service.command.ManualEntryCommand;
import app.mediatracker.feature.library.model.LibraryEntryStatus;
import app.mediatracker.feature.search.core.dto.SearchResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(app.mediatracker.feature.library.controller.LibraryController.class)
@AutoConfigureMockMvc(addFilters = false)
class LibraryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LibraryService libraryService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

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
        when(libraryService.getLibraryForUser(any(ObjectId.class)))
                .thenReturn(List.of(new LibraryEntryResponse()));

        mockMvc.perform(get("/api/library"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(libraryService).getLibraryForUser(any(ObjectId.class));
    }

    @Test
    void addOrUpdateEntry_validRequest_shouldReturnOk() throws Exception {
        AddLibraryEntryRequest request = validAddRequest();

        when(libraryService.addOrUpdateEntryFromSearchResult(any(), any(), any(), any(), any()))
                .thenReturn(new LibraryEntryResponse());

        mockMvc.perform(post("/api/library")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(libraryService).addOrUpdateEntryFromSearchResult(any(), any(), any(), any(), any());
    }

    @Test
    void addManualEntry_validRequest_shouldReturnOk() throws Exception {
        ManualEntryRequest request = validManualRequest();

        when(libraryService.addManualEntry(any(ManualEntryCommand.class)))
                .thenReturn(new LibraryEntryResponse());

        mockMvc.perform(post("/api/library/manualEntry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(libraryService).addManualEntry(any(ManualEntryCommand.class));
    }

    @Test
    void updateManualEntry_shouldReturnOk() throws Exception {
        ManualEntryRequest request = validManualRequest();

        when(libraryService.updateManualEntry(any(), any()))
                .thenReturn(new LibraryEntryResponse());

        mockMvc.perform(patch("/api/library/manualEntry/{id}", "123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(libraryService).updateManualEntry(any(), any());
    }

    @Test
    void removeEntry_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/library/{id}", "123"))
                .andExpect(status().isNoContent());

        verify(libraryService).removeEntry(any(), any());
    }
}
