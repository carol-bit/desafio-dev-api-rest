package com.dock.digital.domain.model;

import java.util.UUID;

public record Holder(
        UUID id,
        String cpf,
        String name
) {}