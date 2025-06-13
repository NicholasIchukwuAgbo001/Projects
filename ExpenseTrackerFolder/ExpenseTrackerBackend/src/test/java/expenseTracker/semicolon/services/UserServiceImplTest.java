package expenseTracker.semicolon.services;

import expenseTracker.semicolon.Exceptions.UserAlreadyExistsException;
import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.data.repository.UserRepository;
import expenseTracker.semicolon.dtos.requests.UserLoginRequest;
import expenseTracker.semicolon.dtos.requests.UserSignupRequest;
import expenseTracker.semicolon.utils.PasswordUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void registerUser_ShouldSaveUser_WhenEmailIsUnique() {
        UserSignupRequest request = new UserSignupRequest("new@example.com", "New User", "securePassword");
        userService.registerUser(request);

        Optional<User> user = userRepository.findByEmail("new@example.com");
        assertTrue(user.isPresent());
        assertEquals("New User", user.get().getName());
    }

    @Test
    public void registerUser_ShouldThrowException_WhenEmailExists() {
        User existingUser = new User();
        existingUser.setEmail("test@example.com");
        existingUser.setName("John");
        existingUser.setPassword("hashedPassword");
        userRepository.save(existingUser);

        UserSignupRequest request = new UserSignupRequest("test@example.com", "John", "password123");

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(request));
    }
}
