package com.dock.digital.adapters.inbound.rest.dto.mapper;

import com.dock.digital.adapters.inbound.rest.dto.response.AccountResponse;
import com.dock.digital.domain.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoMapper {
    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.id(),
                account.holderCpf(),
                account.accountNumber(),
                account.branch(),
                account.balance(),
                account.status().name(),
                account.isBlocked()
        );
    }

    public AccountResponse toBalanceResponse(Account account) {
        return new AccountResponse(
                account.id(),
                account.holderCpf(),
                account.accountNumber(),
                account.branch(),
                account.balance(),
                account.status().name(),
                account.isBlocked()
        );
    }
}