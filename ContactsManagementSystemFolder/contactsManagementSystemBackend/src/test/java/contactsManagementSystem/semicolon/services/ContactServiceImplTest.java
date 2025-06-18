package contactsManagementSystem.semicolon.services;

import contactsManagementSystem.semicolon.data.models.Contact;
import contactsManagementSystem.semicolon.data.repository.ContactRepository;
import contactsManagementSystem.semicolon.dtos.requests.ContactRequest;
import contactsManagementSystem.semicolon.dtos.responses.ApiResponse;
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

        ApiResponse response = contactService.createContact(request);

        assertTrue(response.isSuccess());
        assertEquals("Contact saved successfully", response.getMessage());

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
}
