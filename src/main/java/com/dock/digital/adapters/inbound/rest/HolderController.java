package com.dock.digital.adapters.inbound.rest;

import com.dock.digital.adapters.inbound.rest.dto.request.HolderRequest;
import com.dock.digital.adapters.inbound.rest.dto.response.HolderResponse;
import com.dock.digital.domain.model.Holder;
import com.dock.digital.domain.ports.input.HolderServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/holders")
@RequiredArgsConstructor
@Tag(name = "Holders", description = "Operations related to the registration and search of holders (clients).")
public class HolderController {

    private final HolderServicePort holderService;

    @PostMapping
    @Operation(
            summary = "Create a new holder",
            description = "Registers a new holder with CPF and name in the system.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Holder created successfully.",
                            content = @Content(schema = @Schema(implementation = HolderResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid data (malformed CPF or null fields)."),
                    @ApiResponse(responseCode = "409", description = "Conflict: Holder with CPF already registered.")
            }
    )
    public ResponseEntity<HolderResponse> create(@RequestBody HolderRequest request) {
        var holder = new Holder(null, request.cpf(), request.name());
        var saved = holderService.createHolder(holder);
        return ResponseEntity.ok(new HolderResponse(saved.id(), saved.cpf(), saved.name()));
    }

    @GetMapping("/{cpf}")
    @Operation(
            summary = "Find holder by CPF",
            description = "Retrieves a holder's data using their CPF.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Holder found successfully.",
                            content = @Content(schema = @Schema(implementation = HolderResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Holder not found."),
                    @ApiResponse(responseCode = "400", description = "Malformed CPF.")
            }
    )
    public ResponseEntity<HolderResponse> find(
            @Parameter(description = "CPF (numbers only) of the holder to be searched", example = "12345678900")
            @PathVariable String cpf
    ) {
        var holder = holderService.findByCpf(cpf);
        return ResponseEntity.ok(new HolderResponse(holder.id(), holder.cpf(), holder.name()));
    }
}