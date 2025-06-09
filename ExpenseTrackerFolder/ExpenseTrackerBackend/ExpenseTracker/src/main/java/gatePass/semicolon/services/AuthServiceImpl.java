package gatePass.semicolon.services;

import gatePass.semicolon.data.models.User;
import gatePass.semicolon.data.repository.UserRepository;
import gatePass.semicolon.dtos.requests.LoginRequest;
import gatePass.semicolon.dtos.requests.RegisterRequest;
import gatePass.semicolon.dtos.responses.ApiResponse;
import gatePass.semicolon.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public ApiResponse signup(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists", null);
        }
        User user = Mapper.map(request);
        userRepository.save(user);
        return new ApiResponse(true, "Signup successful", user.getId());
    }

    @Override
    public ApiResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            return new ApiResponse(false, "Invalid credentials", null);
        }
        return new ApiResponse(true, "Login successful", user.getId());
    }
}
