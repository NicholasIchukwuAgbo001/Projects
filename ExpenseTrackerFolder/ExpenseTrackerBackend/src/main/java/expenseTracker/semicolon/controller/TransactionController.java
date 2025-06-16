package expenseTracker.semicolon.controller;

import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.dtos.responses.TransactionResponse;
import expenseTracker.semicolon.services.TransactionService;
import expenseTracker.semicolon.utils.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/request")
    public ApiResponse addTransaction(@RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.addTransaction(request);
        return new ApiResponse("Transaction added successfully", true, List.of(response));
    }

    @GetMapping("/{userId}")
    public ApiResponse getTransactions(@PathVariable String userId) {
        List<TransactionResponse> transactions = transactionService.getTransactions(userId);
        return new ApiResponse("Transactions retrieved successfully", true, transactions);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteTransaction(@PathVariable String id) {
        return transactionService.deleteTransaction(id);
    }
}
