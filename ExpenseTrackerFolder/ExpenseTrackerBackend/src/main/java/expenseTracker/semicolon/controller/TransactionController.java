package expenseTracker.semicolon.controller;

import expenseTracker.semicolon.dtos.requests.TransactionRequest;
import expenseTracker.semicolon.utils.responses.RegisterResponse;
import expenseTracker.semicolon.services.TransactionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public RegisterResponse addTransaction(@RequestBody TransactionRequest request) {
        return transactionService.addTransaction(request);
    }

    @GetMapping("/{userId}")
    public RegisterResponse getTransactions(@PathVariable String userId) {
        return transactionService.getTransactions(userId);
    }

    @DeleteMapping("/{id}")
    public RegisterResponse deleteTransaction(@PathVariable String id) {
        return transactionService.deleteTransaction(id);
    }
}