package br.com.alessandra.petcare.repository;

import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.alessandra.petcare.model.StatusPet;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    // listar pets de um tutor específico
    List<Pet> findByTutor(Tutor tutor);

    // listar pets por status (ex: DISPONIVEL ou ADOTADO)
    List<Pet> findByStatus(String status);

    // Retorna todos os pets filtrados pelo status informado(disponivel ou adotado)
    List<Pet> findByStatus(StatusPet status);
}
