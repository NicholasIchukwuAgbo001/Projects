package gatePass.semicolon.services;

import gatePass.semicolon.dtos.requests.TransactionRequest;
import gatePass.semicolon.dtos.responses.ApiResponse;

public interface TransactionService {
    ApiResponse addTransaction(TransactionRequest request);
    ApiResponse getTransactionsForUser(Long userId);
    ApiResponse deleteTransaction(Long transactionId);
}
