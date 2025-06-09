package gatePass.semicolon.dtos.requests;

import lombok.Data;

@Data
public class TransactionRequest {
    private Long userId;
    private String description;
    private double amount;
}
