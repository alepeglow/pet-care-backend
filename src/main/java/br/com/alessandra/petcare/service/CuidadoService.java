package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.Cuidado;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.repository.CuidadoRepository;
import br.com.alessandra.petcare.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuidadoService {

    private final CuidadoRepository cuidadoRepository;
    private final PetRepository petRepository;

    public CuidadoService(CuidadoRepository cuidadoRepository,
                          PetRepository petRepository) {
        this.cuidadoRepository = cuidadoRepository;
        this.petRepository = petRepository;
    }

    public Cuidado criar(Cuidado cuidado) {
        // garante que o pet existe
        if (cuidado.getPet() == null || cuidado.getPet().getId() == null) {
            throw new RuntimeException("É obrigatório informar o pet no cuidado.");
        }

        Pet pet = petRepository.findById(cuidado.getPet().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Pet não encontrado com id: " + cuidado.getPet().getId()));

        cuidado.setPet(pet);

        return cuidadoRepository.save(cuidado);
    }

    public List<Cuidado> listarTodos() {
        return cuidadoRepository.findAll();
    }

    public Cuidado buscarPorId(Long id) {
        return cuidadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuidado não encontrado com id: " + id));
    }

    public List<Cuidado> listarPorPet(Long idPet) {
        Pet pet = petRepository.findById(idPet)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com id: " + idPet));

        return cuidadoRepository.findByPet(pet);
    }

    public Cuidado atualizar(Long id, Cuidado dadosAtualizados) {
        Cuidado cuidado = buscarPorId(id);

        cuidado.setTipo(dadosAtualizados.getTipo());
        cuidado.setDescricao(dadosAtualizados.getDescricao());
        cuidado.setData(dadosAtualizados.getData());
        cuidado.setCusto(dadosAtualizados.getCusto());

        if (dadosAtualizados.getPet() != null && dadosAtualizados.getPet().getId() != null) {
            Pet pet = petRepository.findById(dadosAtualizados.getPet().getId())
                    .orElseThrow(() -> new RuntimeException("Pet não encontrado com id: " + dadosAtualizados.getPet().getId()));
            cuidado.setPet(pet);
        }

        return cuidadoRepository.save(cuidado);
    }

    public void deletar(Long id) {
        Cuidado cuidado = buscarPorId(id);
        cuidadoRepository.delete(cuidado);
    }
}
