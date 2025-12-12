package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.Cuidado;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.TipoCuidado;
import br.com.alessandra.petcare.repository.CuidadoRepository;
import br.com.alessandra.petcare.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        Pet pet = validarEPegarPet(cuidado);
        cuidado.setPet(pet);

        validarENormalizar(cuidado);

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
        if (!petRepository.existsById(idPet)) {
            throw new RuntimeException("Pet não encontrado com id: " + idPet);
        }
        return cuidadoRepository.findByPetIdOrderByDataDesc(idPet);
    }

    public List<Cuidado> listarPorTipo(String tipo) {
        TipoCuidado tipoEnum = TipoCuidado.from(tipo);
        return cuidadoRepository.findByTipoOrderByDataDesc(tipoEnum);
    }

    public List<Cuidado> listarPorPetETipo(Long idPet, String tipo) {
        if (!petRepository.existsById(idPet)) {
            throw new RuntimeException("Pet não encontrado com id: " + idPet);
        }
        TipoCuidado tipoEnum = TipoCuidado.from(tipo);
        return cuidadoRepository.findByPetIdAndTipoOrderByDataDesc(idPet, tipoEnum);
    }

    public Cuidado atualizar(Long id, Cuidado dadosAtualizados) {
        Cuidado cuidado = buscarPorId(id);

        // mantém pet atual se não vier no body
        if (dadosAtualizados.getPet() != null && dadosAtualizados.getPet().getId() != null) {
            Pet pet = petRepository.findById(dadosAtualizados.getPet().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Pet não encontrado com id: " + dadosAtualizados.getPet().getId()));
            cuidado.setPet(pet);
        }

        if (dadosAtualizados.getTipo() == null) {
            throw new RuntimeException("O tipo de cuidado é obrigatório.");
        }
        if (dadosAtualizados.getData() == null) {
            throw new RuntimeException("A data do cuidado é obrigatória.");
        }

        cuidado.setTipo(dadosAtualizados.getTipo());
        cuidado.setDescricao(dadosAtualizados.getDescricao());
        cuidado.setData(dadosAtualizados.getData());
        cuidado.setCusto(dadosAtualizados.getCusto());

        validarENormalizar(cuidado);

        return cuidadoRepository.save(cuidado);
    }

    public void deletar(Long id) {
        Cuidado cuidado = buscarPorId(id);
        cuidadoRepository.delete(cuidado);
    }

    // ==========================
    // Regras/Validações
    // ==========================

    private Pet validarEPegarPet(Cuidado cuidado) {
        if (cuidado.getPet() == null || cuidado.getPet().getId() == null) {
            throw new RuntimeException("É obrigatório informar o pet no cuidado.");
        }

        return petRepository.findById(cuidado.getPet().getId())
                .orElseThrow(() -> new RuntimeException(
                        "Pet não encontrado com id: " + cuidado.getPet().getId()));
    }

    private void validarENormalizar(Cuidado cuidado) {
        // tipo obrigatório
        if (cuidado.getTipo() == null) {
            throw new RuntimeException("O tipo de cuidado é obrigatório.");
        }

        // data obrigatória e não pode ser futura
        if (cuidado.getData() == null) {
            throw new RuntimeException("A data do cuidado é obrigatória.");
        }
        if (cuidado.getData().isAfter(LocalDate.now())) {
            throw new RuntimeException("A data do cuidado não pode ser no futuro.");
        }

        // custo não pode ser negativo
        if (cuidado.getCusto() != null && cuidado.getCusto().signum() < 0) {
            throw new RuntimeException("O custo do cuidado não pode ser negativo.");
        }

        // normaliza descrição
        if (cuidado.getDescricao() != null) {
            String desc = cuidado.getDescricao().trim();
            cuidado.setDescricao(desc.isBlank() ? null : desc);
        }

        // descrição obrigatória para alguns tipos
        if (precisaDescricao(cuidado.getTipo())) {
            if (cuidado.getDescricao() == null || cuidado.getDescricao().isBlank()) {
                throw new RuntimeException("Descrição é obrigatória para o tipo: " + cuidado.getTipo());
            }
        }
    }

    private boolean precisaDescricao(TipoCuidado tipo) {
        return switch (tipo) {
            case VACINA, MEDICACAO, VERMIFUGO, CONSULTA, OUTRO -> true;
            default -> false; // BANHO, TOSA podem ser sem descrição
        };
    }
}
