package contactsManagementSystem.semicolon.dtos.responses;

import lombok.Data;

@Data
public class RegisterResponse {
    private String message;
    private String userId;
    private String email;
    private boolean success;
}
