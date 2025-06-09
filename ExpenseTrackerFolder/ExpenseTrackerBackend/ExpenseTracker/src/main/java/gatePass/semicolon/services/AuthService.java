package gatePass.semicolon.services;

import gatePass.semicolon.dtos.requests.LoginRequest;
import gatePass.semicolon.dtos.requests.RegisterRequest;
import gatePass.semicolon.dtos.responses.ApiResponse;

public interface AuthService {
    ApiResponse signup(RegisterRequest request);
    ApiResponse login(LoginRequest request);

}
