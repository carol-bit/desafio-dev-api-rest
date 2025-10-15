package com.dock.digital.adapters.inbound.rest;

import com.dock.digital.adapters.inbound.rest.dto.request.HolderRequest;
import com.dock.digital.adapters.inbound.rest.dto.response.HolderResponse;
import com.dock.digital.domain.exceptions.ResourceNotFoundException;
import com.dock.digital.domain.model.Holder;
import com.dock.digital.domain.ports.input.HolderServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HolderController.class)
@DisplayName("Holder Controller Tests")
class HolderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HolderServicePort holderService;

    private final UUID holderId = UUID.randomUUID();
    private final String holderCpf = "12345678900";
    private final String holderName = "Maria Teste";
    private Holder mockHolder;
    private HolderRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockHolder = new Holder(holderId, holderCpf, holderName);
        mockRequest = new HolderRequest(holderCpf, holderName);
    }

    @Test
    @DisplayName("Should return 200 and the created holder data")
    void create_ShouldReturnOk() throws Exception {
        when(holderService.createHolder(any(Holder.class))).thenReturn(mockHolder);

        mockMvc.perform(post("/holders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(holderId.toString()))
                .andExpect(jsonPath("$.cpf").value(holderCpf))
                .andExpect(jsonPath("$.name").value(holderName));

        verify(holderService, times(1)).createHolder(any(Holder.class));
    }


    @Test
    @DisplayName("Should return 200 and holder data when found by CPF")
    void findByCpf_ShouldReturnOk() throws Exception {
        when(holderService.findByCpf(holderCpf)).thenReturn(mockHolder);

        mockMvc.perform(get("/holders/{cpf}", holderCpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(holderId.toString()))
                .andExpect(jsonPath("$.cpf").value(holderCpf));

        verify(holderService, times(1)).findByCpf(holderCpf);
    }

    @Test
    @DisplayName("Should return 404 when holder not found by CPF")
    void findByCpf_ShouldReturnNotFound() throws Exception {
        when(holderService.findByCpf(holderCpf)).thenThrow(new ResourceNotFoundException("Holder not found by CPF."));

        mockMvc.perform(get("/holders/{cpf}", holderCpf)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(holderService, times(1)).findByCpf(holderCpf);
    }
}