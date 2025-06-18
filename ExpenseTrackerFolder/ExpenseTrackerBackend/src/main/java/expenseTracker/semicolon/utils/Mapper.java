package expenseTracker.semicolon.utils;

import expenseTracker.semicolon.data.models.Transaction;
import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.dtos.requests.RegisterRequest;
import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.TransactionResponse;

public final class Mapper {

    public static User mapToUser(RegisterRequest request) {
        return new User(
                null,
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getAge()
        );
    }

    public static Transaction mapToTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUserId(request.getUserId());
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        return transaction;
    }


    public static TransactionResponse toTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setDescription(transaction.getDescription());
        response.setAmount(transaction.getAmount());
        response.setUserId(transaction.getUserId());
        return response;
    }
}