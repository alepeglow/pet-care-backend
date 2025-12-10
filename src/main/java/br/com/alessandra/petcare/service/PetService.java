package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.StatusPet;
import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.repository.PetRepository;
import br.com.alessandra.petcare.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;

    public PetService(PetRepository petRepository, TutorRepository tutorRepository) {
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public Pet buscarPorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com id: " + id));
    }

    public List<Pet> listarPorTutor(Long idTutor) {
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com id: " + idTutor));
        return petRepository.findByTutor(tutor);
    }

    public Pet criar(Pet pet) {
        // se vier tutor no JSON, garante que existe
        if (pet.getTutor() != null && pet.getTutor().getId() != null) {
            Long idTutor = pet.getTutor().getId();
            Tutor tutor = tutorRepository.findById(idTutor)
                    .orElseThrow(() -> new RuntimeException("Tutor não encontrado com id: " + idTutor));
            pet.setTutor(tutor);
        }

        // se não vier status, define padrão DISPONIVEL
        if (pet.getStatus() == null) {
            pet.setStatus(StatusPet.DISPONIVEL);
        }

        return petRepository.save(pet);
    }

    public Pet atualizar(Long id, Pet dadosAtualizados) {
        Pet pet = buscarPorId(id);

        pet.setNome(dadosAtualizados.getNome());
        pet.setEspecie(dadosAtualizados.getEspecie());
        pet.setRaca(dadosAtualizados.getRaca());
        pet.setIdade(dadosAtualizados.getIdade());
        pet.setStatus(dadosAtualizados.getStatus());
        pet.setDataEntrada(dadosAtualizados.getDataEntrada());

        // atualizar tutor, se informado
        if (dadosAtualizados.getTutor() != null && dadosAtualizados.getTutor().getId() != null) {
            Long idTutor = dadosAtualizados.getTutor().getId();
            Tutor tutor = tutorRepository.findById(idTutor)
                    .orElseThrow(() -> new RuntimeException("Tutor não encontrado com id: " + idTutor));
            pet.setTutor(tutor);
        } else {
            pet.setTutor(null);
        }

        return petRepository.save(pet);
    }

    public void deletar(Long id) {
        Pet pet = buscarPorId(id);
        // depois podemos colocar regra: não deletar se tiver adoção/eventos
        petRepository.delete(pet);
    }

    public Pet adotarPet(Long idPet, Long idTutor) {

        // 1. Buscar o pet
        Pet pet = buscarPorId(idPet);

        // 2. Verificar se já foi adotado
        if (pet.getStatus() == StatusPet.ADOTADO) {
            throw new RuntimeException("Este pet já foi adotado.");
        }

        // 3. Buscar tutor direto pelo repository
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com id: " + idTutor));

        // 4. Atualizar status e vínculo
        pet.setTutor(tutor);
        pet.setStatus(StatusPet.ADOTADO);

        // 5. Salvar
        return petRepository.save(pet);
    }

}
