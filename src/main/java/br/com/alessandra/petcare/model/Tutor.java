package br.com.alessandra.petcare.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import java.util.List;

@Entity
@Table(name = "tutor")
@Data
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    private String telefone;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "O e-mail é obrigatório")
    @Column(unique = true)
    private String email;

    private String endereco;

    @OneToMany(mappedBy = "tutor")
    @JsonIgnore
    private List<Pet> pets;

    @OneToMany(mappedBy = "tutor")
    @JsonIgnore
    private List<Adocao> adocoes;

}
