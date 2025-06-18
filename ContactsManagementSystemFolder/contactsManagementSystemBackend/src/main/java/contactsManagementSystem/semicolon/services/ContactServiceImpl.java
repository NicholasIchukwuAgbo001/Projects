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
        return new ApiResponse("Contact saved successfully", true);
    }

    @Override
    public List<Contact> getUserContacts(String userId) {
        return contactRepository.findAllByUserId(userId);
    }

    @Override
    public ApiResponse deleteContact(ContactRequest request) {
        if (request.getUserId() == null || request.getEmail() == null) {
            throw new IllegalArgumentException("User ID and email must be provided.");
        }

        Contact contact = contactRepository.findByUserIdAndEmail(request.getUserId(), request.getEmail());
        if (contact == null) {
            throw new IllegalStateException("Contact not found.");
        }

        contactRepository.delete(contact);
        return new ApiResponse("Contact deleted successfully", true);
    }

    public ApiResponse deleteContactById(String contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalStateException("Contact not found"));
        contactRepository.delete(contact);
        return new ApiResponse("Contact deleted successfully", true);
    }
}
