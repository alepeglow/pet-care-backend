package br.com.alessandra.petcare.repository;

import br.com.alessandra.petcare.model.Cuidado;
import br.com.alessandra.petcare.model.TipoCuidado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuidadoRepository extends JpaRepository<Cuidado, Long> {

    // Lista todos os cuidados de um pet específico (mais recente primeiro)
    List<Cuidado> findByPetIdOrderByDataDesc(Long petId);

    // Lista cuidados por tipo (mais recente primeiro)
    List<Cuidado> findByTipoOrderByDataDesc(TipoCuidado tipo);

    // Lista cuidados por pet e tipo (mais recente primeiro)
    List<Cuidado> findByPetIdAndTipoOrderByDataDesc(Long petId, TipoCuidado tipo);
}
