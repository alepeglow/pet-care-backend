package br.com.alessandra.petcare.repository;

import br.com.alessandra.petcare.model.Cuidado;
import br.com.alessandra.petcare.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuidadoRepository extends JpaRepository<Cuidado, Long> {

    // Lista todos os cuidados de um pet específico
    List<Cuidado> findByPet(Pet pet);
}
