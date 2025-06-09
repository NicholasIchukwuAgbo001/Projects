package gatePass.semicolon.dtos.responses;

import lombok.Data;

@Data
public class SummaryResponse {
    private double balance;
    private double totalIncome;
    private double totalExpense;
}
