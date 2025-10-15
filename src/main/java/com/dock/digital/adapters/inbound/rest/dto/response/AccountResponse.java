package com.dock.digital.adapters.inbound.rest.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String holderCpf,
        String accountNumber,
        String branch,
        BigDecimal balance,
        String status,
        boolean isBlocked
) {}