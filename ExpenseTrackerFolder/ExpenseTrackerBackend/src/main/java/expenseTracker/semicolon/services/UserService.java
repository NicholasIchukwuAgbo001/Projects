package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.data.repository.UserRepository;
import expenseTracker.semicolon.dtos.requests.LoginRequest;
import expenseTracker.semicolon.dtos.requests.RegisterRequest;
import expenseTracker.semicolon.dtos.responses.LoginResponse;
import expenseTracker.semicolon.dtos.responses.RegisterResponse;
import expenseTracker.semicolon.exceptions.AppException;
import expenseTracker.semicolon.utils.Mapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int MAX_PASSWORD_LENGTH = 72;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public RegisterResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new RegisterResponse(false, "Email already registered");
        }

        validatePasswordLength(request.getPassword());

        User user = Mapper.mapToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            userRepository.save(user);
            return new RegisterResponse(true, "User registered successfully");
        } catch (Exception e) {
            return new RegisterResponse(false, "Registration failed: " + e.getMessage());
        }
    }

    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException("Invalid credentials");
        }

        return new LoginResponse(true, user.getId(), user.getName(), "Login successful");
    }

    private void validatePasswordLength(String password) {
        if (password == null) {
            throw new AppException("Password cannot be null");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new AppException("Password exceeds maximum length of " + MAX_PASSWORD_LENGTH + " characters");
        }
    }
}