package gatePass.semicolon.services;

import gatePass.semicolon.data.models.Transaction;
import gatePass.semicolon.data.models.User;
import gatePass.semicolon.data.repository.TransactionRepository;
import gatePass.semicolon.data.repository.UserRepository;
import gatePass.semicolon.dtos.requests.TransactionRequest;
import gatePass.semicolon.dtos.responses.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTransaction_shouldSucceed_whenUserExists() {
        TransactionRequest request = new TransactionRequest();
        request.setUserId(1L);
        request.setDescription("Test expense");
        request.setAmount(-500.0);

        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);

        ApiResponse response = transactionService.addTransaction(request);

        assertTrue(response.isSuccess());
        assertEquals("Transaction added", response.getMessage());
    }

    @Test
    void addTransaction_shouldFail_whenUserNotFound() {
        TransactionRequest request = new TransactionRequest();
        request.setUserId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ApiResponse response = transactionService.addTransaction(request);

        assertFalse(response.isSuccess());
        assertEquals("User not found", response.getMessage());
    }
}