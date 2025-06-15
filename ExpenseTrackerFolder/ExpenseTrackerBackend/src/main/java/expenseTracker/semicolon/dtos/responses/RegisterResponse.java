package expenseTracker.semicolon.dtos.responses;

import expenseTracker.semicolon.data.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private boolean success;
    private String message;
    private List<Transaction> transactions;

    public RegisterResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.transactions = null;
    }
}
