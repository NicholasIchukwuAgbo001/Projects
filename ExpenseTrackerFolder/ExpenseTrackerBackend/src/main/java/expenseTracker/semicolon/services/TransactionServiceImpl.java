package expenseTracker.semicolon.services;

import expenseTracker.semicolon.data.models.Transaction;
import expenseTracker.semicolon.data.repository.TransactionRepository;
import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.TransactionResponse;
import expenseTracker.semicolon.utils.ApiResponse;
import expenseTracker.semicolon.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public TransactionResponse addTransaction(TransactionRequest request) {
        if (request == null || request.getUserId() == null || request.getDescription() == null) {
            throw new IllegalArgumentException("Missing required fields in request");
        }

        System.out.println("TransactionRequest: " + request);

        Transaction transaction = Mapper.mapToTransaction(request);
        transactionRepository.save(transaction);
        return Mapper.toTransactionResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactions(String userId) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);
        return transactions.stream()
                .map(Mapper::toTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse deleteTransaction(String id) {
        if (!transactionRepository.existsById(id)) {
            return new ApiResponse("Transaction not found", false);
        }
        transactionRepository.deleteById(id);
        return new ApiResponse("Transaction deleted successfully", true);
    }
}
