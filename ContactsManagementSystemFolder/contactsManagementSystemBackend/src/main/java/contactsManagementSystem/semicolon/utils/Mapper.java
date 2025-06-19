package contactsManagementSystem.semicolon.utils;

import contactsManagementSystem.semicolon.data.models.Contact;
import contactsManagementSystem.semicolon.data.models.User;
import contactsManagementSystem.semicolon.dtos.requests.ContactRequest;
import contactsManagementSystem.semicolon.dtos.responses.ContactResponse;
import contactsManagementSystem.semicolon.dtos.responses.LoginResponse;
import contactsManagementSystem.semicolon.dtos.responses.RegisterResponse;

public class Mapper {

    public static RegisterResponse mapToRegisterResponse(User user) {
        RegisterResponse response = new RegisterResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setMessage("Signup successful");
        response.setSuccess(true);
        return response;
    }

    public static LoginResponse mapToLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setMessage("Login successful");
        response.setSuccess(true);
        return response;
    }

    public static ContactResponse mapToContactResponse(Contact contact) {
        ContactResponse response = new ContactResponse();
        response.setId(contact.getId());
        response.setName(contact.getName());
        response.setPhoneNumber(contact.getPhoneNumber());
        response.setEmail(contact.getEmail());
        response.setJobTitle(contact.getJobTitle());
        response.setUserId(contact.getUserId());
        return response;
    }


    public static Contact map(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmail(request.getEmail());
        contact.setJobTitle(request.getJobTitle());
        contact.setUserId(request.getUserId());
        return contact;
    }
}
