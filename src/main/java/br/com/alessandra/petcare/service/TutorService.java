package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;

    // injeção de dependência via construtor
    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    // LISTAR TODOS
    public List<Tutor> listarTodos() {
        return tutorRepository.findAll();
    }

    // BUSCAR POR ID
    public Tutor buscarPorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com id: " + id));
    }

    // CRIAR (com validação de e-mail único)
    public Tutor criar(Tutor tutor) {

        var existente = tutorRepository.findByEmail(tutor.getEmail());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um tutor cadastrado com este e-mail.");
        }

        return tutorRepository.save(tutor);
    }

    // ATUALIZAR
    public Tutor atualizar(Long id, Tutor dadosAtualizados) {
        Tutor tutor = buscarPorId(id); // reaproveita validação

        // se trocar o e-mail, valida se já não existe outro usando esse e-mail
        if (!tutor.getEmail().equals(dadosAtualizados.getEmail())) {
            var existente = tutorRepository.findByEmail(dadosAtualizados.getEmail());
            if (existente.isPresent()) {
                throw new RuntimeException("Já existe um tutor cadastrado com este e-mail.");
            }
        }

        tutor.setNome(dadosAtualizados.getNome());
        tutor.setTelefone(dadosAtualizados.getTelefone());
        tutor.setEmail(dadosAtualizados.getEmail());
        tutor.setEndereco(dadosAtualizados.getEndereco());

        return tutorRepository.save(tutor);
    }

    // EXCLUIR
    public void deletar(Long id) {
        Tutor tutor = buscarPorId(id); // garante que existe
        // no futuro a gente pode colocar regra: não deletar se tiver pets adotados
        tutorRepository.delete(tutor);
    }
}
