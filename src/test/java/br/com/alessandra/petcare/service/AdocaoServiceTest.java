package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.exception.NotFoundException;
import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.repository.AdocaoRepository;
import br.com.alessandra.petcare.repository.PetRepository;
import br.com.alessandra.petcare.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdocaoServiceTest {

    @Mock
    private AdocaoRepository adocaoRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private AdocaoService adocaoService;

    @Test
    void listarPorPet_deveRetornarListaQuandoPetExiste() {
        Long idPet = 1L;

        Pet pet = new Pet();
        pet.setId(idPet);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));
        when(adocaoRepository.findByPetOrderByDataAdocaoDesc(pet))
                .thenReturn(List.of(new Adocao(), new Adocao()));

        List<Adocao> lista = adocaoService.listarPorPet(idPet);

        assertNotNull(lista);
        assertEquals(2, lista.size());

        verify(petRepository).findById(idPet);
        verify(adocaoRepository).findByPetOrderByDataAdocaoDesc(pet);

        // garante que não salvou/alterou nada
        verify(adocaoRepository, never()).save(any());
        verify(adocaoRepository, never()).delete(any());
    }

    @Test
    void listarPorPet_deveFalharQuandoPetNaoExiste() {
        Long idPet = 1L;

        when(petRepository.findById(idPet)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> adocaoService.listarPorPet(idPet));

        assertTrue(ex.getMessage().contains("Pet não encontrado"));

        verify(petRepository).findById(idPet);
        verifyNoInteractions(adocaoRepository);
    }

    @Test
    void listarPorTutor_deveRetornarListaQuandoTutorExiste() {
        Long idTutor = 10L;

        Tutor tutor = new Tutor();
        tutor.setId(idTutor);

        when(tutorRepository.findById(idTutor)).thenReturn(Optional.of(tutor));
        when(adocaoRepository.findByTutorOrderByDataAdocaoDesc(tutor))
                .thenReturn(List.of(new Adocao()));

        List<Adocao> lista = adocaoService.listarPorTutor(idTutor);

        assertNotNull(lista);
        assertEquals(1, lista.size());

        verify(tutorRepository).findById(idTutor);
        verify(adocaoRepository).findByTutorOrderByDataAdocaoDesc(tutor);

        // garante que não salvou/alterou nada
        verify(adocaoRepository, never()).save(any());
        verify(adocaoRepository, never()).delete(any());
    }

    @Test
    void listarPorTutor_deveFalharQuandoTutorNaoExiste() {
        Long idTutor = 10L;

        when(tutorRepository.findById(idTutor)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> adocaoService.listarPorTutor(idTutor));

        assertTrue(ex.getMessage().contains("Tutor não encontrado"));

        verify(tutorRepository).findById(idTutor);
        verifyNoInteractions(adocaoRepository);
    }
}
