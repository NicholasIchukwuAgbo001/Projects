package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.models.Transaction;
import expenseTracker.semicolon.data.repository.TransactionRepository;
import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.RegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void cleanDatabase() {
        transactionRepository.deleteAll();
    }

    @Test
    public void testAddTransaction_savesTransaction() {
        TransactionRequest request = new TransactionRequest();
        request.setDescription("Lunch");
        request.setAmount(1200.00);
        request.setUserId("1122");

        RegisterResponse response = transactionService.addTransaction(request);

        assertTrue(response.isSuccess());
        assertEquals("Transaction added successfully", response.getMessage());

        List<Transaction> transactions = transactionRepository.findAllByUserId("1122");
        assertEquals(1, transactions.size());
        assertEquals("Lunch", transactions.getFirst().getDescription());
        assertEquals(1200.00, transactions.getFirst().getAmount());
    }

    @Test
    public void testGetTransactions_returnsAllUserTransactions() {
        Transaction t1 = new Transaction(null, "Ewa", 100.0, "1111");
        Transaction t2 = new Transaction(null, "Ewa", 100.0, "1111");
        Transaction t3 = new Transaction(null, "Garri", 30.0, "3333");
        transactionRepository.save(t1);
        transactionRepository.save(t2);
        transactionRepository.save(t3);

        RegisterResponse response = transactionService.getTransactions("1111");

        assertTrue(response.isSuccess());
        assertEquals("Transactions retrieved", response.getMessage());

        List<Transaction> transactions = response.getTransactions();
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
    }

    @Test
    public void testDeleteTransaction_successfulDeletion() {
        Transaction t = new Transaction(null, "Subscription", 1500.00, "3333");
        Transaction saved = transactionRepository.save(t);

        RegisterResponse response = transactionService.deleteTransaction(saved.getId());

        assertTrue(response.isSuccess());
        assertEquals("Transaction deleted successfully", response.getMessage());

        assertFalse(transactionRepository.existsById(saved.getId()));
    }

    @Test
    public void testDeleteTransaction_transactionNotFound() {
        RegisterResponse response = transactionService.deleteTransaction("0000");

        assertFalse(response.isSuccess());
        assertEquals("Transaction not found", response.getMessage());
    }
}



