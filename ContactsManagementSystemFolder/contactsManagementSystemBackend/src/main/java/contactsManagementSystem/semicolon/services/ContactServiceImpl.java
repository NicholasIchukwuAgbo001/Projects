package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.data.models.Contact;
import contactsManagementSystem.semicolon.data.repository.ContactRepository;
import contactsManagementSystem.semicolon.dtos.requests.ContactRequest;
import contactsManagementSystem.semicolon.dtos.responses.ApiResponse;
import contactsManagementSystem.semicolon.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Override
    public ApiResponse createContact(ContactRequest request) {
        Contact contact = Mapper.map(request);
        contactRepository.save(contact);
        ApiResponse response = new ApiResponse();
        response.setMessage("Contact saved successfully");
        response.setSuccess(true);
        return response;
    }

    @Override
    public List<Contact> getUserContacts(String userId) {
        return contactRepository.findAllByUserId(userId);
    }
}
