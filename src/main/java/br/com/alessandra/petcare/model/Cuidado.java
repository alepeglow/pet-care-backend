package br.com.alessandra.petcare.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "O tipo de cuidado é obrigatório")
    private String tipo; // Ex: BANHO, TOSA, VACINA, CONSULTA

    private String descricao;

    @NotNull(message = "A data do cuidado é obrigatória")
    @Column(name = "data_cuidado")
    private LocalDate data;

    private BigDecimal custo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pet")
    @JsonIgnoreProperties({"tutor", "adocoes"})
    private Pet pet;
}
