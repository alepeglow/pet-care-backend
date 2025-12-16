package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.Tutor;
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
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    // ✅ só precisa se teu deletar usa essa verificação
    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private TutorService tutorService;

    @Test
    void listarTodos_deveRetornarListaOrdenadaPorId() {
        Tutor t1 = new Tutor(); t1.setId(1L);
        Tutor t2 = new Tutor(); t2.setId(2L);

        when(tutorRepository.findAllByOrderByIdAsc()).thenReturn(List.of(t1, t2));

        List<Tutor> lista = tutorService.listarTodos();

        assertEquals(2, lista.size());
        assertEquals(1L, lista.get(0).getId());
        assertEquals(2L, lista.get(1).getId());

        verify(tutorRepository).findAllByOrderByIdAsc();
    }

    @Test
    void buscarPorId_deveRetornarTutorQuandoExiste() {
        Tutor tutor = new Tutor();
        tutor.setId(10L);

        when(tutorRepository.findById(10L)).thenReturn(Optional.of(tutor));

        Tutor achado = tutorService.buscarPorId(10L);

        assertEquals(10L, achado.getId());
        verify(tutorRepository).findById(10L);
    }

    @Test
    void buscarPorId_deveFalharQuandoNaoExiste() {
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tutorService.buscarPorId(99L));

        assertTrue(ex.getMessage().toLowerCase().contains("tutor não encontrado"));
        verify(tutorRepository).findById(99L);
    }

    @Test
    void criar_deveSalvarQuandoEmailNaoExiste() {
        Tutor novo = new Tutor();
        novo.setNome("Ana");
        novo.setEmail("ana@email.com");

        when(tutorRepository.findByEmail("ana@email.com")).thenReturn(Optional.empty());
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(inv -> inv.getArgument(0));

        Tutor salvo = tutorService.criar(novo);

        assertEquals("Ana", salvo.getNome());
        assertEquals("ana@email.com", salvo.getEmail());

        verify(tutorRepository).findByEmail("ana@email.com");
        verify(tutorRepository).save(novo);
    }

    @Test
    void criar_deveFalharQuandoEmailJaExiste() {
        Tutor existente = new Tutor();
        existente.setId(1L);
        existente.setEmail("x@email.com");

        Tutor novo = new Tutor();
        novo.setEmail("x@email.com");

        when(tutorRepository.findByEmail("x@email.com")).thenReturn(Optional.of(existente));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tutorService.criar(novo));

        assertTrue(ex.getMessage().toLowerCase().contains("já existe"));
        verify(tutorRepository).findByEmail("x@email.com");
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void atualizar_deveAtualizarQuandoEmailNaoMuda() {
        Tutor atual = new Tutor();
        atual.setId(1L);
        atual.setEmail("a@email.com");

        Tutor dados = new Tutor();
        dados.setNome("Novo Nome");
        dados.setTelefone("9999");
        dados.setEmail("a@email.com");
        dados.setEndereco("Rua X");

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(atual));
        when(tutorRepository.save(any(Tutor.class))).thenAnswer(inv -> inv.getArgument(0));

        Tutor atualizado = tutorService.atualizar(1L, dados);

        assertEquals("Novo Nome", atualizado.getNome());
        assertEquals("9999", atualizado.getTelefone());
        assertEquals("a@email.com", atualizado.getEmail());
        assertEquals("Rua X", atualizado.getEndereco());

        verify(tutorRepository).findById(1L);
        verify(tutorRepository, never()).findByEmail(anyString());
        verify(tutorRepository).save(atual);
    }

    @Test
    void atualizar_deveFalharQuandoNovoEmailJaExiste() {
        Tutor atual = new Tutor();
        atual.setId(1L);
        atual.setEmail("antigo@email.com");

        Tutor outro = new Tutor();
        outro.setId(2L);
        outro.setEmail("novo@email.com");

        Tutor dados = new Tutor();
        dados.setNome("Nome");
        dados.setEmail("novo@email.com");

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(atual));
        when(tutorRepository.findByEmail("novo@email.com")).thenReturn(Optional.of(outro));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tutorService.atualizar(1L, dados));

        assertTrue(ex.getMessage().toLowerCase().contains("já existe"));
        verify(tutorRepository).findById(1L);
        verify(tutorRepository).findByEmail("novo@email.com");
        verify(tutorRepository, never()).save(any());
    }

    @Test
    void deletar_deveDeletarQuandoNaoTemPets() {
        Tutor tutor = new Tutor();
        tutor.setId(1L);

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(petRepository.existsByTutor_Id(1L)).thenReturn(false);

        tutorService.deletar(1L);

        verify(tutorRepository).findById(1L);
        verify(petRepository).existsByTutor_Id(1L);
        verify(tutorRepository).delete(tutor);
    }

    @Test
    void deletar_deveFalharQuandoTemPets() {
        Tutor tutor = new Tutor();
        tutor.setId(1L);

        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(petRepository.existsByTutor_Id(1L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tutorService.deletar(1L));

        assertTrue(ex.getMessage().toLowerCase().contains("possui pets"));
        verify(tutorRepository).findById(1L);
        verify(petRepository).existsByTutor_Id(1L);
        verify(tutorRepository, never()).delete(any());
    }
}
