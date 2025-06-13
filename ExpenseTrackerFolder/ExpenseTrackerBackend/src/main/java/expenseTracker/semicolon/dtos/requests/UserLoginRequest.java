package expenseTracker.semicolon.dtos.requests;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String email;
    private String password;
}
