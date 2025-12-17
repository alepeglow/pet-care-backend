package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.StatusAdocao;
import br.com.alessandra.petcare.model.StatusPet;
import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.repository.AdocaoRepository;
import br.com.alessandra.petcare.repository.PetRepository;
import br.com.alessandra.petcare.repository.TutorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private TutorRepository tutorRepository;

    @Mock
    private AdocaoRepository adocaoRepository;

    @InjectMocks
    private PetService petService;

    // =========================
    // ADOTAR
    // =========================

    @Test
    void adotarPet_deveAdotarQuandoDisponivel() {
        Long idPet = 1L;
        Long idTutor = 10L;

        Pet pet = new Pet();
        pet.setId(idPet);
        pet.setStatus(StatusPet.DISPONIVEL);

        Tutor tutor = new Tutor();
        tutor.setId(idTutor);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));
        when(tutorRepository.findById(idTutor)).thenReturn(Optional.of(tutor));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));
        when(adocaoRepository.save(any(Adocao.class))).thenAnswer(inv -> inv.getArgument(0));

        Pet resultado = petService.adotarPet(idPet, idTutor);

        assertEquals(StatusPet.ADOTADO, resultado.getStatus());
        assertNotNull(resultado.getTutor());
        assertEquals(idTutor, resultado.getTutor().getId());

        verify(petRepository).save(pet);

        ArgumentCaptor<Adocao> captor = ArgumentCaptor.forClass(Adocao.class);
        verify(adocaoRepository).save(captor.capture());

        Adocao adocaoSalva = captor.getValue();
        assertEquals(pet, adocaoSalva.getPet());
        assertEquals(tutor, adocaoSalva.getTutor());
        assertEquals(StatusAdocao.ATIVA, adocaoSalva.getStatus());
        assertEquals(LocalDate.now(), adocaoSalva.getDataAdocao());
    }

    @Test
    void adotarPet_deveFalharQuandoPetNaoExiste() {
        Long idPet = 1L;

        when(petRepository.findById(idPet)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.adotarPet(idPet, 10L));

        assertTrue(ex.getMessage().toLowerCase().contains("pet não encontrado"));

        verifyNoInteractions(tutorRepository);
        verifyNoInteractions(adocaoRepository);
        verify(petRepository, never()).save(any());
    }

    @Test
    void adotarPet_deveFalharQuandoPetJaMarcadoComoAdotado() {
        Long idPet = 1L;

        Pet pet = new Pet();
        pet.setId(idPet);
        pet.setStatus(StatusPet.ADOTADO);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.adotarPet(idPet, 99L));

        assertEquals("Este pet já está marcado como ADOTADO.", ex.getMessage());

        verify(tutorRepository, never()).findById(anyLong());
        verify(petRepository, never()).save(any());
        verify(adocaoRepository, never()).save(any());
    }



    @Test
    void adotarPet_deveFalharQuandoTutorNaoExiste() {
        Long idPet = 1L;
        Long idTutor = 10L;

        Pet pet = new Pet();
        pet.setId(idPet);
        pet.setStatus(StatusPet.DISPONIVEL);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));
        when(tutorRepository.findById(idTutor)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.adotarPet(idPet, idTutor));

        assertTrue(ex.getMessage().toLowerCase().contains("tutor não encontrado"));

        verify(adocaoRepository, never()).save(any());
        verify(petRepository, never()).save(any()); // não adota se tutor não existe
    }


    @Test
    void adotarPet_deveFalharQuandoJaExisteAdocaoAtivaMesmoPetDisponivel() {
        Long idPet = 1L;
        Long idTutor = 10L;

        Pet pet = new Pet();
        pet.setId(idPet);
        pet.setStatus(StatusPet.DISPONIVEL);

        Adocao adocaoAtiva = new Adocao();
        adocaoAtiva.setId(100L);
        adocaoAtiva.setPet(pet);
        adocaoAtiva.setStatus(StatusAdocao.ATIVA);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));
        when(adocaoRepository.findFirstByPetAndStatusOrderByDataAdocaoDesc(pet, StatusAdocao.ATIVA))
                .thenReturn(Optional.of(adocaoAtiva));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.adotarPet(idPet, idTutor));

        assertTrue(ex.getMessage().toLowerCase().contains("adoção ativa"));

        verify(tutorRepository, never()).findById(anyLong());
        verify(petRepository, never()).save(any());
        verify(adocaoRepository, never()).save(any());
    }

    // =========================
    // DEVOLVER
    // =========================

    @Test
    void devolverPet_deveFalharQuandoPetNaoExiste() {
        Long idPet = 1L;

        when(petRepository.findById(idPet)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.devolverPet(idPet));

        assertTrue(ex.getMessage().toLowerCase().contains("pet não encontrado"));

        verifyNoInteractions(adocaoRepository);
        verify(petRepository, never()).save(any());
    }

    @Test
    void devolverPet_deveFalharQuandoNaoEstaAdotado() {
        Long idPet = 1L;

        Pet pet = new Pet();
        pet.setId(idPet);
        pet.setStatus(StatusPet.DISPONIVEL);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.devolverPet(idPet));

        assertEquals("Não é possível devolver: este pet não está adotado.", ex.getMessage());

        verify(adocaoRepository, never()).findFirstByPetAndStatusOrderByDataAdocaoDesc(any(), any());
        verify(adocaoRepository, never()).save(any());
        verify(petRepository, never()).save(any());
    }


    @Test
    void devolverPet_deveFalharQuandoNaoHaAdocaoAtiva() {
        Long idPet = 1L;

        Pet pet = new Pet();
        pet.setId(idPet);
        pet.setStatus(StatusPet.ADOTADO);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));
        when(adocaoRepository.findFirstByPetAndStatusOrderByDataAdocaoDesc(pet, StatusAdocao.ATIVA))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> petService.devolverPet(idPet));

        assertEquals("Não foi encontrada uma adoção ativa para este pet.", ex.getMessage());

        verify(adocaoRepository, never()).save(any());
        verify(petRepository, never()).save(any());
    }


    @Test
    void devolverPet_deveEncerrarAdocaoEDeixarPetDisponivel() {
        Long idPet = 1L;

        Tutor tutorAtual = new Tutor();
        tutorAtual.setId(10L);

        Pet pet = new Pet();
        pet.setId(idPet);
        pet.setStatus(StatusPet.ADOTADO);
        pet.setTutor(tutorAtual);

        Adocao adocaoAtiva = new Adocao();
        adocaoAtiva.setId(100L);
        adocaoAtiva.setPet(pet);
        adocaoAtiva.setTutor(tutorAtual);
        adocaoAtiva.setStatus(StatusAdocao.ATIVA);

        when(petRepository.findById(idPet)).thenReturn(Optional.of(pet));
        when(adocaoRepository.findFirstByPetAndStatusOrderByDataAdocaoDesc(pet, StatusAdocao.ATIVA))
                .thenReturn(Optional.of(adocaoAtiva));
        when(adocaoRepository.save(any(Adocao.class))).thenAnswer(inv -> inv.getArgument(0));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        Pet resultado = petService.devolverPet(idPet);

        assertEquals(StatusPet.DISPONIVEL, resultado.getStatus());
        assertNull(resultado.getTutor());

        assertEquals(StatusAdocao.ENCERRADA, adocaoAtiva.getStatus());
        assertEquals(LocalDate.now(), adocaoAtiva.getDataDevolucao());

        verify(adocaoRepository).save(adocaoAtiva);
        verify(petRepository).save(pet);
    }




}
