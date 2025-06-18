package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.dtos.requests.LoginRequest;
import contactsManagementSystem.semicolon.dtos.requests.RegisterRequest;
import contactsManagementSystem.semicolon.dtos.responses.LoginResponse;
import contactsManagementSystem.semicolon.dtos.responses.RegisterResponse;

public interface UserService {
    RegisterResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}
