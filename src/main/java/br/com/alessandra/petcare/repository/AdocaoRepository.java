package br.com.alessandra.petcare.repository;

import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.StatusAdocao;
import br.com.alessandra.petcare.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdocaoRepository extends JpaRepository<Adocao, Long> {

    // Histórico de um pet (ordenado do mais recente para o mais antigo)
    List<Adocao> findByPetOrderByDataAdocaoDesc(Pet pet);

    // Histórico de um tutor
    List<Adocao> findByTutorOrderByDataAdocaoDesc(Tutor tutor);

    // Adoção ativa de um pet (para devolução)
    Optional<Adocao> findFirstByPetAndStatusOrderByDataAdocaoDesc(Pet pet, StatusAdocao status);
}
