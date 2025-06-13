package expenseTracker.semicolon.controller;

import expenseTracker.semicolon.Exceptions.UserAlreadyExistsException;
import expenseTracker.semicolon.dtos.requests.UserLoginRequest;
import expenseTracker.semicolon.dtos.requests.UserSignupRequest;
import expenseTracker.semicolon.dtos.responses.ApiResponse;
import expenseTracker.semicolon.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody UserSignupRequest signupRequest) {
        try {
            userService.registerUser(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "User registered successfully"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody UserLoginRequest loginRequest) {
        boolean authenticated = userService.authenticateUser(loginRequest);
        if (authenticated) {
            return ResponseEntity.ok(new ApiResponse(true, "Login successful"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid email or password"));
        }
    }
}
