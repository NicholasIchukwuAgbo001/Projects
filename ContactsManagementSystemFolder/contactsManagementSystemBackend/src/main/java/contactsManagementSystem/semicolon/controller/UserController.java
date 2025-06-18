package contactsManagementSystem.semicolon.controller;

import contactsManagementSystem.semicolon.dtos.requests.LoginRequest;
import contactsManagementSystem.semicolon.dtos.requests.RegisterRequest;
import contactsManagementSystem.semicolon.dtos.responses.LoginResponse;
import contactsManagementSystem.semicolon.dtos.responses.RegisterResponse;
import contactsManagementSystem.semicolon.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
