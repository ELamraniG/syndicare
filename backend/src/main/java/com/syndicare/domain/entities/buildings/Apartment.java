package com.syndicare.domain.entities.buildings;

import com.syndicare.domain.entities.users.User;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "apartments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal surface;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    @JsonBackReference
    private Building building;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id")
    private User resident;
}
