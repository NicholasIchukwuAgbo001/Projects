package contactsManagementSystem.semicolon.dtos.responses;

import lombok.Data;

@Data
public class ContactResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String jobTitle;
    private String userId;
}
