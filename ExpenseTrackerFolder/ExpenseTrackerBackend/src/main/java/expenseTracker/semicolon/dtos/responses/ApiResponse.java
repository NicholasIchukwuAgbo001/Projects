package expenseTracker.semicolon.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private boolean success;
    private List<TransactionResponse> transactions;

    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.transactions = null;
    }
}

