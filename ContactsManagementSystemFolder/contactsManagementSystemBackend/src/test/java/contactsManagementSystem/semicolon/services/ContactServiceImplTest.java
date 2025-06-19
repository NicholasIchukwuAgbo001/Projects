package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.data.models.Contact;
import contactsManagementSystem.semicolon.data.repository.ContactRepository;
import contactsManagementSystem.semicolon.dtos.requests.ContactRequest;
import contactsManagementSystem.semicolon.dtos.responses.ApiResponse;
import contactsManagementSystem.semicolon.dtos.responses.ContactResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(ContactServiceImpl.class)
public class ContactServiceImplTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactServiceImpl contactService;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
    }

    @Test
    void testCreateContact() {
        ContactRequest request = new ContactRequest();
        request.setName("Nicholas");
        request.setEmail("niko@gmail.com");
        request.setPhoneNumber("0704567890");
        request.setJobTitle("Engineer");
        request.setUserId("1");

        ContactResponse response = contactService.createContact(request);

        assertNotNull(response);
        assertEquals("Nicholas", response.getName());
        assertTrue(response.getId() != null && !response.getId().isEmpty());

        List<Contact> savedContacts = contactRepository.findAllByUserId("1");
        assertEquals(1, savedContacts.size());
        assertEquals("Nicholas", savedContacts.getFirst().getName());
    }

    @Test
    void testGetUserContacts() {
        Contact contact = new Contact();
        contact.setName("Nicholas");
        contact.setEmail("niko@gmail.com");
        contact.setPhoneNumber("0907654321");
        contact.setJobTitle("Manager");
        contact.setUserId("1");

        contactRepository.save(contact);

        List<Contact> contacts = contactService.getUserContacts("1");

        assertEquals(1, contacts.size());
        assertEquals("Nicholas", contacts.getFirst().getName());
    }

    @Test
    void testGetMultipleContactsForUser() {
        Contact contact1 = new Contact();
        contact1.setName("Nicholas");
        contact1.setPhoneNumber("123");
        contact1.setUserId("user123");

        Contact contact2 = new Contact();
        contact2.setName("Jane");
        contact2.setPhoneNumber("456");
        contact2.setUserId("user123");

        contactRepository.saveAll(List.of(contact1, contact2));

        List<Contact> contacts = contactService.getUserContacts("user123");
        assertEquals(2, contacts.size());
    }

    @Test
    void testDeleteContactById() {
        Contact contact = new Contact();
        contact.setName("John");
        contact.setEmail("john@example.com");
        contact.setPhoneNumber("987");
        contact.setUserId("userX");
        Contact saved = contactRepository.save(contact);

        ApiResponse response = contactService.deleteContactById(saved.getId());

        assertTrue(response.isSuccess());
        assertEquals(0, contactRepository.findAllByUserId("userX").size());
    }

    @Test
    void testDeleteContactByUserIdAndEmail() {
        Contact contact = new Contact();
        contact.setName("Alice");
        contact.setEmail("alice@example.com");
        contact.setPhoneNumber("555");
        contact.setJobTitle("Dev");
        contact.setUserId("userY");
        contactRepository.save(contact);

        ContactRequest deleteRequest = new ContactRequest();
        deleteRequest.setUserId("userY");
        deleteRequest.setEmail("alice@example.com");

        ApiResponse response = contactService.deleteContact(deleteRequest);

        assertTrue(response.isSuccess());
        assertEquals("Contact deleted successfully", response.getMessage());
        assertTrue(contactRepository.findAllByUserId("userY").isEmpty());
    }

    @Test
    void testDeleteContactThrowsForMissingUserIdOrEmail() {
        ContactRequest invalidRequest = new ContactRequest();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            contactService.deleteContact(invalidRequest);
        });

        assertEquals("User ID and email must be provided.", exception.getMessage());
    }

    @Test
    void testDeleteContactThrowsWhenNotFound() {
        ContactRequest request = new ContactRequest();
        request.setUserId("unknown");
        request.setEmail("notfound@example.com");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            contactService.deleteContact(request);
        });

        assertEquals("Contact not found.", exception.getMessage());
    }
}
