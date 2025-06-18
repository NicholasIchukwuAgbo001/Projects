package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.repository.TransactionRepository;
import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.TransactionResponse;
import expenseTracker.semicolon.dtos.responses.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class TransactionServiceImplTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        transactionService = new TransactionServiceImpl(transactionRepository);
    }

    @Test
    void testAddTransactionSuccessfully() {
        TransactionRequest request = new TransactionRequest();
        request.setUserId("1122");
        request.setAmount(2500.0);
        request.setDescription("Garri Shopping");

        TransactionResponse response = transactionService.addTransaction(request);

        assertNotNull(response);
        assertEquals("1122", response.getUserId());
        assertEquals("Garri Shopping", response.getDescription());
    }

    @Test
    void testGetTransactions() {
        TransactionRequest request = new TransactionRequest();
        request.setUserId("1133");
        request.setAmount(5000.0);
        request.setDescription("Bill Payment");

        transactionService.addTransaction(request);

        List<TransactionResponse> transactions = transactionService.getTransactions("1133");

        assertEquals(1, transactions.size());
        assertEquals("Bill Payment", transactions.getFirst().getDescription());
    }

    @Test
    void testDeleteTransaction() {
        TransactionRequest request = new TransactionRequest();
        request.setUserId("1144");
        request.setAmount(1200.0);
        request.setDescription("Internet Bill");

        TransactionResponse savedTransaction = transactionService.addTransaction(request);

        ApiResponse response = transactionService.deleteTransaction(savedTransaction.getId());

        assertTrue(response.isSuccess());
        assertEquals("Transaction deleted successfully", response.getMessage());
    }

    @Test
    void testDeleteNonExistentTransaction() {
        ApiResponse response = transactionService.deleteTransaction("invalid-id");

        assertFalse(response.isSuccess());
        assertEquals("Transaction not found", response.getMessage());
    }
}
