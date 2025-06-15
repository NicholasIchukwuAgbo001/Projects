package expenseTracker.semicolon.utils;

import expenseTracker.semicolon.data.models.Transaction;
import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.dtos.requests.RegisterRequest;
import expenseTracker.semicolon.dtos.requests.TransactionRequest;

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
        return new Transaction(
                null,
                request.getDescription(),
                request.getAmount(),
                request.getUserId()
        );
    }
}
