package contactsManagementSystem.semicolon.dtos.requests;

import lombok.Data;

@Data
public class ContactRequest {
    private String name;
    private String phoneNumber;
    private String email;
    private String jobTitle;
    private String userId;
}
