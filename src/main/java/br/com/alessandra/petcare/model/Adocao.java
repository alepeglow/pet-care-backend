package br.com.alessandra.petcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "adocao")
@Data
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PET adotado
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pet", nullable = false)
    @JsonIgnoreProperties({"tutor", "adocoes", "hibernateLazyInitializer", "handler"})
    private Pet pet;

    // TUTOR que adotou
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutor", nullable = false)
    @JsonIgnoreProperties({"pets", "adocoes", "hibernateLazyInitializer", "handler"})
    private Tutor tutor;

    @Column(name = "data_adocao", nullable = false)
    private LocalDate dataAdocao;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;  // null enquanto estiver com esse tutor

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAdocao status; // ATIVA ou ENCERRADA
}
