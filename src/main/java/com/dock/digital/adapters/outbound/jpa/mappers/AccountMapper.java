package com.dock.digital.adapters.outbound.jpa.mappers;

import com.dock.digital.adapters.outbound.jpa.entities.AccountEntity;
import com.dock.digital.domain.model.Account;

public class AccountMapper {

    public static AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.id(),
                account.holderCpf(),
                account.accountNumber(),
                account.branch(),
                account.balance(),
                AccountEntity.AccountStatus.valueOf(account.status().name()),
                account.isBlocked()
        );
    }

    public static Account toDomain(AccountEntity entity) {
        return new Account(
                entity.getId(),
                entity.getHolderCpf(),
                entity.getAccountNumber(),
                entity.getBranch(),
                entity.getBalance(),
                Account.Status.valueOf(entity.getStatus().name()),
                entity.getIsBlocked()
        );
    }
}