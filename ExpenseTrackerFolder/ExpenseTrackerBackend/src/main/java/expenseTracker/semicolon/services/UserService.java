package expenseTracker.semicolon.services;

import expenseTracker.semicolon.dtos.requests.LoginRequest;
import expenseTracker.semicolon.dtos.requests.RegisterRequest;
import expenseTracker.semicolon.dtos.responses.LoginResponse;
import expenseTracker.semicolon.dtos.responses.RegisterResponse;

public interface UserService {
    RegisterResponse registerUser(RegisterRequest request);
    LoginResponse loginUser(LoginRequest request);
}
