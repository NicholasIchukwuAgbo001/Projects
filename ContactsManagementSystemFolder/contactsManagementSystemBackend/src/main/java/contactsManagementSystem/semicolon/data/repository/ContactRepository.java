package contactsManagementSystem.semicolon.data.repository;

import contactsManagementSystem.semicolon.data.models.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContactRepository extends MongoRepository<Contact, String> {
    List<Contact> findAllByUserId(String userId);
    Contact findByUserIdAndEmail(String userId, String email);
}
