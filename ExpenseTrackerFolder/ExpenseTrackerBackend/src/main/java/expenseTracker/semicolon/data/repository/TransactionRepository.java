package expenseTracker.semicolon.data.repository;

import expenseTracker.semicolon.data.models.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findAllByUserId(String userId);
}
