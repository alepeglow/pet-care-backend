package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.repository.AdocaoRepository;
import br.com.alessandra.petcare.repository.PetRepository;
import br.com.alessandra.petcare.repository.TutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdocaoService {

    private final AdocaoRepository adocaoRepository;
    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;

    public AdocaoService(AdocaoRepository adocaoRepository,
                         PetRepository petRepository,
                         TutorRepository tutorRepository) {
        this.adocaoRepository = adocaoRepository;
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
    }

    // Histórico de adoções de um PET (mais recente primeiro)
    public List<Adocao> listarPorPet(Long idPet) {
        Pet pet = petRepository.findById(idPet)
                .orElseThrow(() -> new RuntimeException("Pet não encontrado com id: " + idPet));

        return adocaoRepository.findByPetOrderByDataAdocaoDesc(pet);
    }

    // Histórico de adoções de um TUTOR (mais recente primeiro)
    public List<Adocao> listarPorTutor(Long idTutor) {
        Tutor tutor = tutorRepository.findById(idTutor)
                .orElseThrow(() -> new RuntimeException("Tutor não encontrado com id: " + idTutor));

        return adocaoRepository.findByTutorOrderByDataAdocaoDesc(tutor);
    }
}
