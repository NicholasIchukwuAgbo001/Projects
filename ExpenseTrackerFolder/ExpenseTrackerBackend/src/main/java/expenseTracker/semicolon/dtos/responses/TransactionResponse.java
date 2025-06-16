package expenseTracker.semicolon.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {
    private String id;
    private String description;
    private double amount;
    private String userId;
}
