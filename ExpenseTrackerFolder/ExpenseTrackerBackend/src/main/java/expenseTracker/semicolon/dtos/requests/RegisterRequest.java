package expenseTracker.semicolon.dtos.requests;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private int age;
}