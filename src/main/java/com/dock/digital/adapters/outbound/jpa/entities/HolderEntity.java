package com.dock.digital.adapters.outbound.jpa.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "holder")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
}