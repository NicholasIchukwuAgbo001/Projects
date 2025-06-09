package gatePass.semicolon.data.repository;

import gatePass.semicolon.data.models.Transaction;
import gatePass.semicolon.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser(User user);
}
