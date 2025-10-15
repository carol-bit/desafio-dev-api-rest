package com.dock.digital.adapters.inbound.rest.dto.request;

import java.math.BigDecimal;

public record MovementRequest(BigDecimal amount) {}