package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.exception.BusinessException;
import br.com.alessandra.petcare.exception.NotFoundException;
import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.repository.PetRepository;
import br.com.alessandra.petcare.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final PetRepository petRepository;

    public TutorService(TutorRepository tutorRepository, PetRepository petRepository) {
        this.tutorRepository = tutorRepository;
        this.petRepository = petRepository;
    }

    public List<Tutor> listarTodos() {
        return tutorRepository.findAllByOrderByIdAsc();
    }

    public Tutor buscarPorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tutor não encontrado com id: " + id));
    }

    public Tutor criar(Tutor tutor) {
        if (tutorRepository.findByEmail(tutor.getEmail()).isPresent()) {
            throw new BusinessException("Já existe um tutor cadastrado com este e-mail.");
        }
        return tutorRepository.save(tutor);
    }

    public Tutor atualizar(Long id, Tutor dadosAtualizados) {
        Tutor tutor = buscarPorId(id);

        String emailAtual = tutor.getEmail();
        String novoEmail = dadosAtualizados.getEmail();

        // se trocar o e-mail, valida duplicidade (mas ignora se for o próprio tutor)
        if (novoEmail != null && !novoEmail.equals(emailAtual)) {
            tutorRepository.findByEmail(novoEmail).ifPresent(outro -> {
                if (!outro.getId().equals(id)) {
                    throw new BusinessException("Já existe um tutor cadastrado com este e-mail.");
                }
            });
        }

        tutor.setNome(dadosAtualizados.getNome());
        tutor.setTelefone(dadosAtualizados.getTelefone());
        tutor.setEmail(novoEmail);
        tutor.setEndereco(dadosAtualizados.getEndereco());

        return tutorRepository.save(tutor);
    }

    @Transactional
    public void deletar(Long id) {
        Tutor tutor = buscarPorId(id);

        // checa se existe algum pet vinculado a esse tutor
        if (petRepository.existsByTutor_Id(id)) {
            throw new BusinessException("Não é possível deletar: este tutor possui pets associados.");
        }

        tutorRepository.delete(tutor);
    }
}
