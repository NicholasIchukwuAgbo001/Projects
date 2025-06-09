package gatePass.semicolon.services;

import gatePass.semicolon.data.models.User;
import gatePass.semicolon.data.repository.UserRepository;
import gatePass.semicolon.dtos.requests.RegisterRequest;
import gatePass.semicolon.dtos.responses.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signup_shouldSucceed_whenEmailIsNew() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setName("John");
        request.setPassword("1234");
        request.setAge(30);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        ApiResponse response = authService.signup(request);

        assertTrue(response.isSuccess());
        assertEquals("Signup successful", response.getMessage());
    }

    @Test
    void signup_shouldFail_whenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        ApiResponse response = authService.signup(request);

        assertFalse(response.isSuccess());
        assertEquals("Email already exists", response.getMessage());
    }
}
