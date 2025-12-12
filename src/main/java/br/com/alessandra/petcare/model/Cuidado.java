package br.com.alessandra.petcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cuidado")
@Data
public class Cuidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O tipo de cuidado é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCuidado tipo; // BANHO, TOSA, VACINA, CONSULTA...

    private String descricao;

    @NotNull(message = "A data do cuidado é obrigatória")
    @Column(name = "data_cuidado", nullable = false)
    private LocalDate data;

    @Column(precision = 10, scale = 2)
    private BigDecimal custo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pet", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "tutor", "adocoes", "cuidados"})
    private Pet pet;
}
