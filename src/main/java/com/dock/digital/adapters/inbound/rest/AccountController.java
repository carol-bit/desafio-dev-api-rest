package com.dock.digital.adapters.inbound.rest;

import com.dock.digital.adapters.inbound.rest.dto.mapper.AccountDtoMapper;
import com.dock.digital.adapters.inbound.rest.dto.request.AccountRequest;
import com.dock.digital.adapters.inbound.rest.dto.response.AccountResponse;
import com.dock.digital.adapters.inbound.rest.dto.request.MovementRequest;
import com.dock.digital.domain.model.Account;
import com.dock.digital.domain.model.Transaction;
import com.dock.digital.domain.ports.input.AccountServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management and movement operations")
public class AccountController {

    private final AccountServicePort service;
    private final AccountDtoMapper mapper;

    @Operation(summary = "Creates a new account for a holder", description = "The account is created in ACTIVE state with zero balance.")
    @ApiResponse(responseCode = "201", description = "Account created successfully", content = @Content(schema = @Schema(implementation = AccountResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., missing or incorrect CPF)")
    @ApiResponse(responseCode = "404", description = "Holder not found")
    @PostMapping
    public ResponseEntity<AccountResponse> create(@RequestBody AccountRequest request) {
        Account account = service.createAccountWithHolderCpf(request.holderCpf());
        return ResponseEntity.status(201).body(mapper.toResponse(account));
    }


    @Operation(summary = "Deposits an amount into the account", description = "Increases the account balance. No limit validations for deposits.")
    @ApiResponse(responseCode = "200", description = "Deposit successfully performed", content = @Content(schema = @Schema(implementation = AccountResponse.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "400", description = "Invalid amount (e.g., zero or negative)")
    @ApiResponse(responseCode = "422", description = "Deposit not allowed (e.g., account is blocked or inactive)")
    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(
            @Parameter(description = "Account ID") @PathVariable UUID id,
            @RequestBody MovementRequest req
    ) {
        Account updated = service.deposit(id, req.amount());
        return ResponseEntity.ok(mapper.toResponse(updated));
    }


    @Operation(summary = "Performs a withdrawal from the account", description = "Decreases the account balance, subject to limit and balance validations.")
    @ApiResponse(responseCode = "200", description = "Withdrawal successfully performed", content = @Content(schema = @Schema(implementation = AccountResponse.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "400", description = "Invalid amount (e.g., zero or negative or exceeds individual limit)")
    @ApiResponse(responseCode = "422", description = "Withdrawal unauthorized (e.g., insufficient balance, daily limit exceeded, or inactive account)")
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(
            @Parameter(description = "Account ID") @PathVariable UUID id,
            @RequestBody MovementRequest req
    ) {
        Account updated = service.withdraw(id, req.amount());
        return ResponseEntity.ok(mapper.toResponse(updated));
    }


    @Operation(summary = "Closes an account", description = "Changes the account status to CLOSED.")
    @ApiResponse(responseCode = "200", description = "Account successfully closed", content = @Content(schema = @Schema(implementation = AccountResponse.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "422", description = "Account cannot be closed (e.g., balance is not zero)")
    @PostMapping("/{id}/close")
    public ResponseEntity<AccountResponse> close(
            @Parameter(description = "Account ID") @PathVariable UUID id
    ) {
        Account updated = service.closeAccount(id);
        return ResponseEntity.ok(mapper.toResponse(updated));
    }


    @Operation(summary = "Consults the transaction statement by period", description = "Returns a list of transactions (withdrawals and deposits) within the specified time range.")
    @ApiResponse(responseCode = "200", description = "Statement returned successfully", content = @Content(schema = @Schema(implementation = Transaction.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @ApiResponse(responseCode = "400", description = "Invalid period (e.g., start date is after end date or incorrect format)")
    @GetMapping("/{id}/statement")
    public ResponseEntity<List<Transaction>> getStatement(
            @Parameter(description = "Account ID") @PathVariable UUID id,
            @Parameter(description = "Start of the period (ISO 8601, e.g., 2025-10-15T09:00:00-03:00)", example = "2025-10-15T09:00:00-03:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @Parameter(description = "End of the period (ISO 8601, e.g., 2025-10-16T09:00:00-03:00)", example = "2025-10-16T09:00:00-03:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end
    ) {
        List<Transaction> list = service.getStatement(id, start, end);
        return ResponseEntity.ok(list);
    }


    @Operation(summary = "Consults the current account balance", description = "Returns the current balance and account status.")
    @ApiResponse(responseCode = "200", description = "Balance successfully returned", content = @Content(schema = @Schema(implementation = AccountResponse.class)))
    @ApiResponse(responseCode = "404", description = "Account not found")
    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> getBalance(
            @Parameter(description = "Account ID") @PathVariable UUID id
    ) {
        return ResponseEntity.ok(mapper.toBalanceResponse(service.findById(id)));
    }

}