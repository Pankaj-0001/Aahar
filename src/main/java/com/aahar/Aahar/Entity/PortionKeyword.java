package com.aahar.Aahar.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "portion_keywords")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortionKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Enumerated(EnumType.STRING)
    private PortionMapping.PortionType type;

    @Enumerated(EnumType.STRING)
    private PortionMapping.PortionSize size;


}
