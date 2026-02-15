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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;

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

    @Test
    void searchUser_shouldReturnUser_whenFoundInDB() throws Exception {
        mockMvc.perform(get("/api/user/search").param("partName", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users[0].username").value("TestUser123"));
    }
}