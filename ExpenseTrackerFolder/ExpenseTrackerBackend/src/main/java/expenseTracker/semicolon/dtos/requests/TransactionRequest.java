package expenseTracker.semicolon.dtos.requests;

import lombok.Data;

@Data
public class TransactionRequest {
    private String userId;
    private String description;
    private double amount;
}
