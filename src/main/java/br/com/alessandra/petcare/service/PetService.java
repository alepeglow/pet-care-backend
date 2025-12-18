package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.exception.BusinessException;
import br.com.alessandra.petcare.exception.NotFoundException;
import br.com.alessandra.petcare.model.*;
import br.com.alessandra.petcare.repository.AdocaoRepository;
import br.com.alessandra.petcare.repository.CuidadoRepository;
import br.com.alessandra.petcare.repository.PetRepository;
import br.com.alessandra.petcare.repository.TutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.alessandra.petcare.model.StatusAdocao;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final AdocaoRepository adocaoRepository;
    private final CuidadoRepository cuidadoRepository;

    public PetService(PetRepository petRepository,
                      TutorRepository tutorRepository,
                      AdocaoRepository adocaoRepository,
                      CuidadoRepository cuidadoRepository){
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
        this.adocaoRepository = adocaoRepository;
        this.cuidadoRepository = cuidadoRepository;
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public List<Pet> listarPorStatus(StatusPet status) {
        return petRepository.findByStatus(status);
    }

    public List<Pet> listarDisponiveis() {
        return listarPorStatus(StatusPet.DISPONIVEL);
    }

    public List<Pet> listarAdotados() {
        return listarPorStatus(StatusPet.ADOTADO);
    }

    public Pet buscarPorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pet não encontrado com id: " + id));
    }

    public List<Pet> listarPorTutor(Long idTutor) {
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new NotFoundException("Tutor não encontrado com id: " + idTutor));
        return petRepository.findByTutor(tutor);
    }

    public Pet criar(Pet pet) {
        // Regra: Pet NÃO nasce adotado e NÃO nasce com tutor.
        if (pet.getStatus() == StatusPet.ADOTADO) {
            throw new BusinessException("Não é permitido criar pet como ADOTADO. Use o endpoint de adoção.");
        }
        if (pet.getTutor() != null) {
            throw new BusinessException("Não é permitido criar pet já vinculado a tutor. Use o endpoint de adoção.");
        }

        pet.setStatus(StatusPet.DISPONIVEL);
        pet.setTutor(null);

        return petRepository.save(pet);
    }

    public Pet atualizar(Long id, Pet dadosAtualizados) {
        Pet pet = buscarPorId(id);

        // Regra: NÃO pode trocar status/tutor no PUT genérico.
        if (dadosAtualizados.getStatus() != null && dadosAtualizados.getStatus() != pet.getStatus()) {
            throw new BusinessException("Não é permitido alterar o STATUS pelo PUT /pets/{id}. Use /adotar ou /devolver.");
        }

        if (dadosAtualizados.getTutor() != null && dadosAtualizados.getTutor().getId() != null) {
            Long novoTutorId = dadosAtualizados.getTutor().getId();
            Long tutorAtualId = pet.getTutor() != null ? pet.getTutor().getId() : null;

            if (!Objects.equals(novoTutorId, tutorAtualId)) {
                throw new BusinessException("Não é permitido alterar o TUTOR pelo PUT /pets/{id}. Use /adotar ou /devolver.");
            }
        }

        // Atualiza apenas dados “cadastro”
        pet.setNome(dadosAtualizados.getNome());
        pet.setEspecie(dadosAtualizados.getEspecie());
        pet.setRaca(dadosAtualizados.getRaca());
        pet.setIdade(dadosAtualizados.getIdade());
        pet.setDataEntrada(dadosAtualizados.getDataEntrada());

        return petRepository.save(pet);
    }
    @Transactional
    public void deletar(Long id) {
        Pet pet = buscarPorId(id);

        // (opcional mas recomendado) não deixa deletar se tiver adoção ativa
        if (adocaoRepository.existsByPet_IdAndStatus(id, StatusAdocao.ATIVA)) {
            throw new BusinessException("Não é possível deletar: pet possui adoção ATIVA.");
        }

        // apaga "filhos" primeiro
        cuidadoRepository.deleteByPetId(id);
        adocaoRepository.deleteByPetId(id);

        // garante que foi pro banco antes do delete do pet
        cuidadoRepository.flush();
        adocaoRepository.flush();

        petRepository.delete(pet);
    }



    @Transactional
    public Pet adotarPet(Long idPet, Long idTutor) {
        Pet pet = buscarPorId(idPet);

        // Regra: se o pet já está ADOTADO, não deixa adotar de novo
        if (pet.getStatus() == StatusPet.ADOTADO) {
            throw new BusinessException("Este pet já está marcado como ADOTADO.");
        }

        // Garantia: 1 adoção ATIVA por pet (pela tabela adocao)
        adocaoRepository.findFirstByPetAndStatusOrderByDataAdocaoDesc(pet, StatusAdocao.ATIVA)
                .ifPresent(a -> {
                    throw new BusinessException("Este pet já possui uma adoção ATIVA (adoção id: " + a.getId() + ").");
                });

        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new NotFoundException("Tutor não encontrado com id: " + idTutor));

        pet.setTutor(tutor);
        pet.setStatus(StatusPet.ADOTADO);
        petRepository.save(pet);

        Adocao adocao = new Adocao();
        adocao.setPet(pet);
        adocao.setTutor(tutor);
        adocao.setDataAdocao(LocalDate.now());
        adocao.setStatus(StatusAdocao.ATIVA);
        adocaoRepository.save(adocao);

        return pet;
    }

    @Transactional
    public Pet devolverPet(Long idPet) {
        Pet pet = buscarPorId(idPet);

        if (pet.getStatus() != StatusPet.ADOTADO) {
            throw new BusinessException("Não é possível devolver: este pet não está adotado.");
        }

        Adocao adocaoAtiva = adocaoRepository
                .findFirstByPetAndStatusOrderByDataAdocaoDesc(pet, StatusAdocao.ATIVA)
                .orElseThrow(() -> new BusinessException("Não foi encontrada uma adoção ativa para este pet."));

        adocaoAtiva.setStatus(StatusAdocao.ENCERRADA);
        adocaoAtiva.setDataDevolucao(LocalDate.now());
        adocaoRepository.save(adocaoAtiva);

        pet.setStatus(StatusPet.DISPONIVEL);
        pet.setTutor(null);

        return petRepository.save(pet);
    }
}
