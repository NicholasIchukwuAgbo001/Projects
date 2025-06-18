package contactsManagementSystem.semicolon.controller;

import contactsManagementSystem.semicolon.data.models.Contact;
import contactsManagementSystem.semicolon.dtos.requests.ContactRequest;
import contactsManagementSystem.semicolon.dtos.responses.ApiResponse;
import contactsManagementSystem.semicolon.services.ContactServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactServiceImpl contactService;

    @PostMapping("/create")
    public ApiResponse createContact(@RequestBody ContactRequest request) {
        return contactService.createContact(request);
    }

    @GetMapping("/{userId}")
    public List<Contact> getUserContacts(@PathVariable String userId) {
        return contactService.getUserContacts(userId);
    }
}
