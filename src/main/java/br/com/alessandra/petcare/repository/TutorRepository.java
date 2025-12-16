package br.com.alessandra.petcare.repository;

import br.com.alessandra.petcare.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    // Busca tutor pelo email (para validar e-mail único)
    Optional<Tutor> findByEmail(String email);
    Optional<Tutor> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);

    // Lista todos os tutores ordenados por ID crescente
    List<Tutor> findAllByOrderByIdAsc();


}
