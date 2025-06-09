package gatePass.semicolon.services;

import gatePass.semicolon.data.models.Transaction;
import gatePass.semicolon.data.models.User;
import gatePass.semicolon.data.repository.TransactionRepository;
import gatePass.semicolon.data.repository.UserRepository;
import gatePass.semicolon.dtos.requests.TransactionRequest;
import gatePass.semicolon.dtos.responses.ApiResponse;
import gatePass.semicolon.utils.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse addTransaction(TransactionRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) return new ApiResponse(false, "User not found", null);

        Transaction transaction = Mapper.map(request);
        transaction.setUser(user);
        transactionRepository.save(transaction);

        return new ApiResponse(true, "Transaction added", transaction.getId());
    }

    @Override
    public ApiResponse getTransactionsForUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return new ApiResponse(false, "User not found", null);

        List<Transaction> transactions = transactionRepository.findAllByUser(user);
        return new ApiResponse(true, "User transactions fetched", transactions);
    }

    @Override
    public ApiResponse deleteTransaction(Long transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            return new ApiResponse(false, "Transaction not found", null);
        }
        transactionRepository.deleteById(transactionId);
        return new ApiResponse(true, "Transaction deleted", null);
    }
}
