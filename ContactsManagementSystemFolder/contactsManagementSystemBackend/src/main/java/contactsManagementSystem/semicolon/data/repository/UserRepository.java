package contactsManagementSystem.semicolon.data.repository;

import contactsManagementSystem.semicolon.data.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
