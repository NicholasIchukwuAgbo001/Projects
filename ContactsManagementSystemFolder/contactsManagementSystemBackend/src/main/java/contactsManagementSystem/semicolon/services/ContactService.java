package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.data.models.Contact;
import contactsManagementSystem.semicolon.dtos.requests.ContactRequest;
import contactsManagementSystem.semicolon.dtos.responses.ApiResponse;
import contactsManagementSystem.semicolon.dtos.responses.ContactResponse;

import java.util.List;

public interface ContactService {
    ContactResponse createContact(ContactRequest request);
    List<Contact> getUserContacts(String userId);
    ApiResponse deleteContact(ContactRequest request);
    ApiResponse deleteContactById(String contactId);
}
