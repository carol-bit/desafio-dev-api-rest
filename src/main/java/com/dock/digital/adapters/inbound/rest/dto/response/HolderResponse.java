package com.dock.digital.adapters.inbound.rest.dto.response;

import java.util.UUID;

public record HolderResponse(UUID id, String cpf, String name) { }