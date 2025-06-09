package gatePass.semicolon.utils;

import gatePass.semicolon.data.models.Transaction;
import gatePass.semicolon.data.models.User;
import gatePass.semicolon.dtos.requests.RegisterRequest;
import gatePass.semicolon.dtos.requests.TransactionRequest;

import java.time.LocalDateTime;

public class Mapper {

    public static User map(RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .age(request.getAge())
                .build();
    }

    public static Transaction map(TransactionRequest request) {
        return Transaction.builder()
                .description(request.getDescription())
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .build();
    }
}
