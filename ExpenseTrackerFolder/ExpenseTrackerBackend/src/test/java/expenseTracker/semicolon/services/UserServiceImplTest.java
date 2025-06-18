package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.data.repository.UserRepository;
import expenseTracker.semicolon.dtos.requests.LoginRequest;
import expenseTracker.semicolon.dtos.requests.RegisterRequest;
import expenseTracker.semicolon.dtos.responses.LoginResponse;
import expenseTracker.semicolon.dtos.responses.RegisterResponse;
import expenseTracker.semicolon.exceptions.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class UserServiceImplTest {

    @Autowired
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void testRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Nicholas");
        request.setEmail("niko@gmail.com");
        request.setPassword("1122");

        RegisterResponse response = userService.registerUser(request);

        assertTrue(response.isSuccess());
        assertEquals("User registered successfully", response.getMessage());

        User savedUser = userRepository.findByEmail("niko@gmail.com").orElse(null);
        assertNotNull(savedUser);
        assertNotEquals("1122", savedUser.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches("1122", savedUser.getPassword()));
    }

    @Test
    void testRegisterDuplicateEmailFails() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Nicholas");
        request.setEmail("niko@gmail.com");
        request.setPassword("1122");

        userService.registerUser(request);
        RegisterResponse response = userService.registerUser(request);

        assertFalse(response.isSuccess());
        assertEquals("Email already registered", response.getMessage());
    }

    @Test
    void testLoginUserSuccessfully() {
        RegisterRequest register = new RegisterRequest();
        register.setName("Nicholas");
        register.setEmail("niko@gmail.com");
        register.setPassword("1122");
        userService.registerUser(register);

        LoginRequest login = new LoginRequest();
        login.setEmail("niko@gmail.com");
        login.setPassword("1122");

        LoginResponse loginResponse = userService.loginUser(login);

        assertTrue(loginResponse.isSuccess());
        assertEquals("Login successful", loginResponse.getMessage());
        assertEquals("niko@gmail.com",
                userRepository.findById(loginResponse.getUserId())
                        .map(User::getEmail)
                        .orElse(null));
    }

    @Test
    void testLoginWithInvalidPasswordThrows() {
        RegisterRequest register = new RegisterRequest();
        register.setName("Nicholas");
        register.setEmail("niko@gmail.com");
        register.setPassword("1122");
        userService.registerUser(register);

        LoginRequest login = new LoginRequest();
        login.setEmail("niko@gmail.com");
        login.setPassword("wrong");

        AppException ex = assertThrows(AppException.class,
                () -> userService.loginUser(login));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void testLoginWithNonExistentEmailThrows() {
        LoginRequest login = new LoginRequest();
        login.setEmail("ghost@example.com");
        login.setPassword("anything");

        AppException ex = assertThrows(AppException.class,
                () -> userService.loginUser(login));
        assertEquals("Invalid credentials", ex.getMessage());
    }

    @Test
    void testRegisterUserWithLongPasswordThrows() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Nicholas");
        request.setEmail("niko@gmail.com");
        request.setPassword("p".repeat(100));

        AppException ex = assertThrows(AppException.class,
                () -> userService.registerUser(request));
        assertTrue(ex.getMessage().contains("exceeds maximum length"));
    }
}
