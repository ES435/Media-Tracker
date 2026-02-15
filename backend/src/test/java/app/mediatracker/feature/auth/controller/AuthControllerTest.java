package app.mediatracker.feature.auth.controller;

import app.mediatracker.feature.auth.dto.LoginRequest;
import app.mediatracker.feature.auth.dto.RegistrationRequest;
import app.mediatracker.feature.auth.service.AuthService;
import app.mediatracker.feature.auth.service.RefreshTokenService;
import app.mediatracker.feature.auth.service.TokenService;
import app.mediatracker.feature.user.model.User;
import app.mediatracker.feature.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit test for AuthController using WebMvcTest.
 * Filters are disabled to test the controller logic without the security filter chain.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_validCredentials_shouldReturnOkAndSetCookies() throws Exception {
        // Arrange: Setup request data and mock user
        LoginRequest request = new LoginRequest("user", "password", false);

        User user = new User();
        user.setId(new ObjectId()); // Essential: Set ID for successful token generation
        user.setUsername("user");

        // Mock successful login call
        when(authService.login("user", "password")).thenReturn(user);

        // Mock token generation (requires User ID and username)
        when(tokenService.generateAccessToken(any(ObjectId.class), anyString()))
                .thenReturn("fake-access-token");

        // Mock refresh token generation and storage
        when(refreshTokenService.createAndStore(any(ObjectId.class)))
                .thenReturn("fake-refresh-token");

        // Act & Assert: Execute POST and verify HTTP status and resulting cookies
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Verify that HTTP-only cookies are correctly set in the response
                .andExpect(cookie().value("accessToken", "fake-access-token"))
                .andExpect(cookie().value("refreshToken", "fake-refresh-token"));
    }

    @Test
    void login_invalidCredentials_shouldReturnBadRequest() throws Exception {
        // Arrange: Setup request with incorrect password
        LoginRequest request = new LoginRequest("user", "wrongpassword", false);

        // Simulate a business logic exception for invalid credentials
        when(authService.login("user", "wrongpassword"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        // Act & Assert: Verify that the error is handled and returns a 400 Bad Request
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_validRequest_shouldReturnOk() throws Exception {
        // Arrange: Setup registration data
        RegistrationRequest request = new RegistrationRequest("user", "password", "password");

        // Act & Assert: Verify registration success message
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful."));

        // Verify that the auth service was actually called with the correct parameters
        verify(authService).register("user", "password", "password");
    }
}