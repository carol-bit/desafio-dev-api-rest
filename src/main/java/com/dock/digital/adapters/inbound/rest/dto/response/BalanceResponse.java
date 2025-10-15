package com.dock.digital.adapters.inbound.rest.dto.response;

import java.math.BigDecimal;

public record BalanceResponse(String accountNumber, String branch, BigDecimal balance) {}