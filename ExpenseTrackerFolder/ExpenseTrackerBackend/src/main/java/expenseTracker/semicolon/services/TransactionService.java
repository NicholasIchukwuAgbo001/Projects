package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.models.Transaction;
import expenseTracker.semicolon.data.repository.TransactionRepository;
import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.RegisterResponse;
import expenseTracker.semicolon.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public RegisterResponse addTransaction(TransactionRequest request) {
        Transaction transaction = Mapper.mapToTransaction(request);

        transactionRepository.save(transaction);

        return new RegisterResponse(true, "Transaction added successfully");
    }

    public RegisterResponse getTransactions(String userId) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        RegisterResponse response = new RegisterResponse(true, "Transactions retrieved");
        response.setTransactions(transactions);
        return response;
    }

    public RegisterResponse deleteTransaction(String id) {
        if (!transactionRepository.existsById(id)) {
            return new RegisterResponse(false, "Transaction not found");
        }
        transactionRepository.deleteById(id);
        return new RegisterResponse(true, "Transaction deleted successfully");
    }
}
