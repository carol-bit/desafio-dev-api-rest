package com.dock.digital.domain.service;

import com.dock.digital.domain.exceptions.ResourceNotFoundException;
import com.dock.digital.domain.model.Holder;
import com.dock.digital.domain.ports.output.HolderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Holder Service Unit Tests (CRUD)")
class HolderServiceTest {

    @Mock
    private HolderRepositoryPort repositoryPort;

    @InjectMocks
    private HolderService holderService;

    private UUID holderId;
    private String holderCpf;
    private Holder holder;

    @BeforeEach
    void setUp() {
        holderId = UUID.randomUUID();
        holderCpf = "12345678900";
        holder = new Holder(holderId, "Teste Holder", holderCpf);
    }

    @Test
    @DisplayName("Should successfully find holder by ID")
    void findById_Success() {
        when(repositoryPort.findById(holderId)).thenReturn(Optional.of(holder));

        Holder result = holderService.findById(holderId);

        assertNotNull(result);
        assertEquals(holderId, result.id());
        verify(repositoryPort, times(1)).findById(holderId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when finding by ID fails")
    void findById_NotFound() {
        when(repositoryPort.findById(holderId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> holderService.findById(holderId));
        verify(repositoryPort, times(1)).findById(holderId);
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException when finding by CPF fails")
    void findByCpf_NotFound() {
        when(repositoryPort.findByCpf(holderCpf)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> holderService.findByCpf(holderCpf));
        verify(repositoryPort, times(1)).findByCpf(holderCpf);
    }

    @Test
    @DisplayName("Should return a list of all holders")
    void listHolders_ReturnsList() {
        List<Holder> mockList = List.of(holder, new Holder(UUID.randomUUID(), "Holder Two", "99999999999"));
        when(repositoryPort.findAll()).thenReturn(mockList);

        List<Holder> result = holderService.listHolders();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(repositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return an empty list when no holders exist")
    void listHolders_ReturnsEmpty() {
        when(repositoryPort.findAll()).thenReturn(List.of());

        List<Holder> result = holderService.listHolders();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should successfully call delete on the repository")
    void deleteHolder_Success() {
        doNothing().when(repositoryPort).delete(holderId);

        holderService.deleteHolder(holderId);

        verify(repositoryPort, times(1)).delete(holderId);
    }
}