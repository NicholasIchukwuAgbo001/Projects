package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.data.models.User;
import contactsManagementSystem.semicolon.data.repository.UserRepository;
import contactsManagementSystem.semicolon.dtos.requests.LoginRequest;
import contactsManagementSystem.semicolon.dtos.requests.RegisterRequest;
import contactsManagementSystem.semicolon.dtos.responses.LoginResponse;
import contactsManagementSystem.semicolon.dtos.responses.RegisterResponse;
import contactsManagementSystem.semicolon.exception.UserAlreadyExistsException;
import contactsManagementSystem.semicolon.exception.UserNotFoundException;
import contactsManagementSystem.semicolon.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException("User already exists");
                });
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        User savedUser = userRepository.save(user);
        return Mapper.mapToRegisterResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        return Mapper.mapToLoginResponse(user);
    }
}