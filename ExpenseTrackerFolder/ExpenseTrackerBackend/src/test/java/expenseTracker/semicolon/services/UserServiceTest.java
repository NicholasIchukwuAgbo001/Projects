package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.data.repository.UserRepository;
import expenseTracker.semicolon.dtos.requests.LoginRequest;
import expenseTracker.semicolon.dtos.requests.RegisterRequest;
import expenseTracker.semicolon.utils.responses.LoginResponse;
import expenseTracker.semicolon.utils.responses.RegisterResponse;
import expenseTracker.semicolon.exceptions.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_successfulRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("niko@gmail.com");
        request.setPassword("2222");
        request.setName("Nicholas Ichukwu");

        RegisterResponse response = userService.registerUser(request);

        assertTrue(response.isSuccess());
        assertEquals("User registered successfully", response.getMessage());

        User savedUser = userRepository.findByEmail("niko@gmail.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("Nicholas Ichukwu", savedUser.getName());
    }

    @Test
    void registerUser_emailAlreadyRegistered() {
        User existingUser = new User();
        existingUser.setEmail("niko@gmail.com");
        existingUser.setPassword(new BCryptPasswordEncoder().encode("1122"));
        existingUser.setName("Nicholas");
        userRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest();
        request.setEmail("niko@gmail.com");
        request.setPassword("1111");
        request.setName("Kodak");

        RegisterResponse response = userService.registerUser(request);

        assertFalse(response.isSuccess());
        assertEquals("Email already registered", response.getMessage());
    }

    @Test
    void registerUser_passwordTooLong_throwsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("niko@gmail.com");
        request.setPassword("1122".repeat(73));
        request.setName("Niko");

        AppException exception = assertThrows(AppException.class, () -> userService.registerUser(request));
        assertTrue(exception.getMessage().contains("exceeds maximum length"));

        assertTrue(userRepository.findByEmail("niko@gmail.com").isEmpty());
    }

    @Test
    void loginUser_successfulLogin() {
        String rawPassword = "1122";
        String encodedPassword = new BCryptPasswordEncoder().encode(rawPassword);

        User user = new User();
        user.setEmail("niko@gmail.com");
        user.setPassword(encodedPassword);
        user.setName("Niko");
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("niko@gmail.com");
        loginRequest.setPassword(rawPassword);

        LoginResponse response = userService.loginUser(loginRequest);

        assertTrue(response.isSuccess());
        assertEquals(user.getId(), response.getUserId());
        assertEquals("Niko", response.getName());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void loginUser_invalidEmail_throwsException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("niko@gmail.com");
        loginRequest.setPassword("1122");

        AppException exception = assertThrows(AppException.class, () -> userService.loginUser(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void loginUser_wrongPassword_throwsException() {
        String encodedPassword = new BCryptPasswordEncoder().encode("0000");

        User user = new User();
        user.setEmail("niko@gmail.com");
        user.setPassword(encodedPassword);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("niko@gmail.com");
        loginRequest.setPassword("1111");

        AppException exception = assertThrows(AppException.class, () -> userService.loginUser(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
    }
}
