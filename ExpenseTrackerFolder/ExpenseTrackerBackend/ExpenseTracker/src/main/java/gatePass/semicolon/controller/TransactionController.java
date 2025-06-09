package gatePass.semicolon.controller;

import gatePass.semicolon.dtos.requests.TransactionRequest;
import gatePass.semicolon.dtos.responses.ApiResponse;
import gatePass.semicolon.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addTransaction(@RequestBody TransactionRequest request) {
        ApiResponse response = transactionService.addTransaction(request);
        return response.isSuccess()
                ? ResponseEntity.status(HttpStatus.CREATED).body(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getTransactionsForUser(userId));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<ApiResponse> deleteTransaction(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.deleteTransaction(transactionId));
    }
}
