package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.data.repository.UserRepository;
import contactsManagementSystem.semicolon.dtos.requests.LoginRequest;
import contactsManagementSystem.semicolon.dtos.requests.RegisterRequest;
import contactsManagementSystem.semicolon.dtos.responses.LoginResponse;
import contactsManagementSystem.semicolon.dtos.responses.RegisterResponse;
import contactsManagementSystem.semicolon.exception.UserAlreadyExistsException;
import contactsManagementSystem.semicolon.exception.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final String testEmail = "niko@example.com";
    private final String testPassword = "1122";

    @BeforeEach
    void cleanUpBefore() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);

        RegisterResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals(testEmail, response.getEmail());
        assertTrue(response.getMessage().contains("Registration successful"));
    }

    @Test
    void testRegisterWithExistingEmailThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(testEmail);
        request.setPassword(testPassword);
        userService.register(request);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));
    }

    @Test
    void testLoginSuccessfully() {
        RegisterRequest register = new RegisterRequest();
        register.setEmail(testEmail);
        register.setPassword(testPassword);
        userService.register(register);

        LoginRequest login = new LoginRequest();
        login.setEmail(testEmail);
        login.setPassword(testPassword);

        LoginResponse response = userService.login(login);

        assertNotNull(response);
        assertEquals(testEmail, response.getEmail());
    }

    @Test
    void testLoginWithInvalidEmailThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("wrong@gmail.com");
        request.setPassword("wrong");

        assertThrows(UserNotFoundException.class, () -> userService.login(request));
    }

    @Test
    void testLoginWithIncorrectPasswordThrowsException() {
        RegisterRequest register = new RegisterRequest();
        register.setEmail(testEmail);
        register.setPassword(testPassword);
        userService.register(register);

        LoginRequest request = new LoginRequest();
        request.setEmail(testEmail);
        request.setPassword("wrongpassword");

        assertThrows(UserNotFoundException.class, () -> userService.login(request));
    }
}
