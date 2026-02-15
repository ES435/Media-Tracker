package app.mediatracker.feature.integration;

import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test for the UserController.
 * This test verifies the interaction between the Web layer, Service layer, and MongoDB.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    /**
     * Clears the database and creates a fresh test user before each test execution.
     */
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User u = User.builder()
                .username("TestUser123")
                .passwordHash("hash")
                .publicList(true)
                .build();
        userRepository.save(u);
    }

    /**
     * Tests the search functionality by simulating a GET request.
     * Verified that the database record is correctly retrieved and mapped to the JSON response.
     */
    @Test
    void searchUser_shouldReturnUser_whenFoundInDB() throws Exception {
        // Act: Execute the search request with a query parameter
        mockMvc.perform(get("/api/user/search").param("partName", "Test"))
                // Assert: Ensure HTTP 200 and verify the content of the returned UserSearchResponse
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[0].username").value("TestUser123"));
    }
}