package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.models.Transaction;
import expenseTracker.semicolon.data.repository.TransactionRepository;
import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.TransactionResponse;
import expenseTracker.semicolon.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    private TransactionServiceImpl transactionService;

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

        TransactionResponse response = transactionService.addTransaction(request);

        assertNotNull(response.getId());
        assertEquals("Lunch", response.getDescription());
        assertEquals(1200.00, response.getAmount());

        List<Transaction> transactions = transactionRepository.findAllByUserId("1122");
        assertEquals(1, transactions.size());
    }

    @Test
    public void testGetTransactions_returnsAllUserTransactions() {
        Transaction t1 = new Transaction(null, "Ewa", 100.0, "1111");
        Transaction t2 = new Transaction(null, "Ewa", 100.0, "1111");
        Transaction t3 = new Transaction(null, "Garri", 30.0, "3333");
        transactionRepository.save(t1);
        transactionRepository.save(t2);
        transactionRepository.save(t3);

        List<TransactionResponse> responses = transactionService.getTransactions("1111");

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Ewa", responses.getFirst().getDescription());
    }

    @Test
    public void testDeleteTransaction_successfulDeletion() {
        Transaction t = new Transaction(null, "Subscription", 1500.00, "3333");
        Transaction saved = transactionRepository.save(t);

        ApiResponse response = transactionService.deleteTransaction(saved.getId());

        assertTrue(response.isSuccess());
        assertEquals("Transaction deleted successfully", response.getMessage());

        assertFalse(transactionRepository.existsById(saved.getId()));
    }

    @Test
    public void testDeleteTransaction_transactionNotFound() {
        ApiResponse response = transactionService.deleteTransaction("0000");

        assertFalse(response.isSuccess());
        assertEquals("Transaction not found", response.getMessage());
    }
}
