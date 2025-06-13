package expenseTracker.semicolon.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {
    private String name;
    private String email;
    private String password;
    private int age;

    public UserSignupRequest(String mail, String john, String password123) {
        this.name = john;
        this.email = mail;
        this.password = password123;
        this.age = 0;
    }
}
