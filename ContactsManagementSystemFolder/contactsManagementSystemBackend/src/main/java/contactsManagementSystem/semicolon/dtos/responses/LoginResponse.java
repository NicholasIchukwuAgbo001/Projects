package contactsManagementSystem.semicolon.dtos.responses;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private String userId;
    private String email;
}
