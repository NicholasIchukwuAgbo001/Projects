package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.data.models.Contact;
import contactsManagementSystem.semicolon.data.repository.ContactRepository;
import contactsManagementSystem.semicolon.dtos.requests.ContactRequest;
import contactsManagementSystem.semicolon.dtos.responses.ApiResponse;
import contactsManagementSystem.semicolon.dtos.responses.ContactResponse;
import contactsManagementSystem.semicolon.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    @Override
    public ContactResponse createContact(ContactRequest request) {
        Contact contact = Mapper.map(request);
        Contact savedContact = contactRepository.save(contact);
        return Mapper.mapToContactResponse(savedContact);
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

        Contact contact = Optional.ofNullable(
                contactRepository.findByUserIdAndEmail(request.getUserId(), request.getEmail())
        ).orElseThrow(() -> new IllegalStateException("Contact not found."));

        contactRepository.delete(contact);
        return new ApiResponse("Contact deleted successfully", true);
    }

    @Override
    public ApiResponse deleteContactById(String contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalStateException("Contact not found."));
        contactRepository.delete(contact);
        return new ApiResponse("Contact deleted successfully", true);
    }
}
