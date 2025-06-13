package expenseTracker.semicolon.services;

import expenseTracker.semicolon.dtos.requests.UserSignupRequest;
import expenseTracker.semicolon.dtos.requests.UserLoginRequest;

public interface UserService {
    void registerUser(UserSignupRequest request);
    boolean authenticateUser(UserLoginRequest request);
}
