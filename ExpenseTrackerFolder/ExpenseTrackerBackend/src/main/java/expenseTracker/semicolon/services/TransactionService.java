package expenseTracker.semicolon.services;

import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.TransactionResponse;
import expenseTracker.semicolon.utils.ApiResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse addTransaction(TransactionRequest request);
    List<TransactionResponse> getTransactions(String userId);
    ApiResponse deleteTransaction(String id);
}
