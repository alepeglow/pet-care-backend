package br.com.alessandra.petcare.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "pet")
@Data
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do pet é obrigatório")
    private String nome;

    @NotBlank(message = "A espécie é obrigatória")
    private String especie; // Ex: cachorro, gato...

    private String raca;

    private Integer idade;

    @NotNull(message = "O status é obrigatório")
    @Enumerated(EnumType.STRING)
    private StatusPet status; // DISPONIVEL, ADOTADO

    @NotNull(message = "A data de entrada é obrigatória")
    @Column(name = "data_entrada")
    private LocalDate dataEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutor")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tutor tutor;
    // pode ser null antes de ser adotado
}
