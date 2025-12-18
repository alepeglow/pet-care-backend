package br.com.alessandra.petcare.repository;

import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.StatusAdocao;
import br.com.alessandra.petcare.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

    //existe alguma adoção cadastrada pra esse pet?
    boolean existsByPetId(Long petId);


    // apaga todas as linhas da tabela adoção onde: pet.id = petId pra adoção encerrada não continuar aparecendo no pet e poder deletar o pet. Ou seja,
    // remove o histórico de adoções
    void deleteByPet_Id(Long petId);

    @Transactional
    @Modifying
    @Query("delete from Adocao a where a.pet.id = :petId")
    void deleteByPetId(@Param("petId") Long petId);

    // responde com true ou false se existe pelo menos uma adoção com: pet.id = petid e status = status(ex: ativa)
    boolean existsByPet_IdAndStatus(Long petId, StatusAdocao status);

}

