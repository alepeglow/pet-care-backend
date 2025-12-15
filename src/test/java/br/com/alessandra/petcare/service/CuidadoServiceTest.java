package br.com.alessandra.petcare.service;

import br.com.alessandra.petcare.model.*;
import br.com.alessandra.petcare.repository.CuidadoRepository;
import br.com.alessandra.petcare.repository.PetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuidadoServiceTest {

    @Mock
    private CuidadoRepository cuidadoRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private CuidadoService cuidadoService;

    // =========================
    // CRIAR
    // =========================

    @Test
    void criar_deveSalvarQuandoDadosValidos() {
        Pet pet = new Pet();
        pet.setId(1L);

        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.BANHO);
        cuidado.setData(LocalDate.now());
        cuidado.setCusto(new BigDecimal("50.00"));
        Pet petNoBody = new Pet();
        petNoBody.setId(1L);
        cuidado.setPet(petNoBody);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(cuidadoRepository.save(any(Cuidado.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuidado salvo = cuidadoService.criar(cuidado);

        assertEquals(TipoCuidado.BANHO, salvo.getTipo());
        assertEquals(LocalDate.now(), salvo.getData());
        assertEquals(1L, salvo.getPet().getId());

        verify(cuidadoRepository).save(any(Cuidado.class));
    }

    @Test
    void criar_deveFalharQuandoPetNaoInformado() {
        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.BANHO);
        cuidado.setData(LocalDate.now());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.criar(cuidado));
        assertEquals("É obrigatório informar o pet no cuidado.", ex.getMessage());

        verify(cuidadoRepository, never()).save(any());
        verify(petRepository, never()).findById(anyLong());
    }

    @Test
    void criar_deveFalharQuandoPetNaoExiste() {
        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.BANHO);
        cuidado.setData(LocalDate.now());
        Pet p = new Pet(); p.setId(1L);
        cuidado.setPet(p);

        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.criar(cuidado));
        assertTrue(ex.getMessage().contains("Pet não encontrado com id: 1"));

        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void criar_deveFalharQuandoTipoNulo() {
        Pet pet = new Pet(); pet.setId(1L);

        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(null);
        cuidado.setData(LocalDate.now());
        Pet p = new Pet(); p.setId(1L);
        cuidado.setPet(p);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.criar(cuidado));
        assertEquals("O tipo de cuidado é obrigatório.", ex.getMessage());

        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void criar_deveFalharQuandoDataNula() {
        Pet pet = new Pet(); pet.setId(1L);

        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.BANHO);
        cuidado.setData(null);
        Pet p = new Pet(); p.setId(1L);
        cuidado.setPet(p);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.criar(cuidado));
        assertEquals("A data do cuidado é obrigatória.", ex.getMessage());

        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void criar_deveFalharQuandoDataFutura() {
        Pet pet = new Pet(); pet.setId(1L);

        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.BANHO);
        cuidado.setData(LocalDate.now().plusDays(1));
        Pet p = new Pet(); p.setId(1L);
        cuidado.setPet(p);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.criar(cuidado));
        assertEquals("A data do cuidado não pode ser no futuro.", ex.getMessage());

        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void criar_deveFalharQuandoCustoNegativo() {
        Pet pet = new Pet(); pet.setId(1L);

        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.BANHO);
        cuidado.setData(LocalDate.now());
        cuidado.setCusto(new BigDecimal("-1"));
        Pet p = new Pet(); p.setId(1L);
        cuidado.setPet(p);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.criar(cuidado));
        assertEquals("O custo do cuidado não pode ser negativo.", ex.getMessage());

        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void criar_deveFalharQuandoTipoExigeDescricaoMasNaoTem() {
        Pet pet = new Pet(); pet.setId(1L);

        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.VACINA);
        cuidado.setData(LocalDate.now());
        cuidado.setDescricao("   "); // vai virar null
        Pet p = new Pet(); p.setId(1L);
        cuidado.setPet(p);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.criar(cuidado));
        assertTrue(ex.getMessage().contains("Descrição é obrigatória para o tipo"));

        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void criar_devePermitirDescricaoVaziaQuandoTipoNaoExige() {
        Pet pet = new Pet(); pet.setId(1L);

        Cuidado cuidado = new Cuidado();
        cuidado.setTipo(TipoCuidado.BANHO); // não exige descrição
        cuidado.setData(LocalDate.now());
        cuidado.setDescricao("   "); // normaliza pra null
        Pet p = new Pet(); p.setId(1L);
        cuidado.setPet(p);

        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(cuidadoRepository.save(any(Cuidado.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuidado salvo = cuidadoService.criar(cuidado);

        assertNull(salvo.getDescricao());
        verify(cuidadoRepository).save(any());
    }

    // =========================
    // ATUALIZAR
    // =========================

    @Test
    void atualizar_deveAtualizarCamposQuandoValidos() {
        Long id = 7L;

        Pet petAtual = new Pet(); petAtual.setId(1L);

        Cuidado existente = new Cuidado();
        existente.setId(id);
        existente.setPet(petAtual);
        existente.setTipo(TipoCuidado.BANHO);
        existente.setData(LocalDate.now().minusDays(1));

        Cuidado dados = new Cuidado();
        dados.setTipo(TipoCuidado.TOSA);
        dados.setData(LocalDate.now());
        dados.setDescricao("Tosa completa");
        dados.setCusto(new BigDecimal("80.00"));
        // sem pet no body => mantém pet atual

        when(cuidadoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(cuidadoRepository.save(any(Cuidado.class))).thenAnswer(inv -> inv.getArgument(0));

        Cuidado atualizado = cuidadoService.atualizar(id, dados);

        assertEquals(TipoCuidado.TOSA, atualizado.getTipo());
        assertEquals(LocalDate.now(), atualizado.getData());
        assertEquals("Tosa completa", atualizado.getDescricao());
        assertEquals(new BigDecimal("80.00"), atualizado.getCusto());
        assertEquals(1L, atualizado.getPet().getId()); // manteve

        verify(cuidadoRepository).save(existente);
    }

    @Test
    void atualizar_deveFalharQuandoCuidadoNaoExiste() {
        when(cuidadoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> cuidadoService.atualizar(99L, new Cuidado()));

        assertTrue(ex.getMessage().contains("Cuidado não encontrado com id: 99"));
        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void atualizar_deveFalharQuandoTipoNulo() {
        Long id = 1L;

        Cuidado existente = new Cuidado();
        existente.setId(id);
        existente.setPet(new Pet()); existente.getPet().setId(1L);

        Cuidado dados = new Cuidado();
        dados.setTipo(null);
        dados.setData(LocalDate.now());

        when(cuidadoRepository.findById(id)).thenReturn(Optional.of(existente));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.atualizar(id, dados));
        assertEquals("O tipo de cuidado é obrigatório.", ex.getMessage());

        verify(cuidadoRepository, never()).save(any());
    }

    @Test
    void atualizar_deveFalharQuandoDataFutura() {
        Long id = 1L;

        Cuidado existente = new Cuidado();
        existente.setId(id);
        existente.setPet(new Pet()); existente.getPet().setId(1L);

        Cuidado dados = new Cuidado();
        dados.setTipo(TipoCuidado.BANHO);
        dados.setData(LocalDate.now().plusDays(1));

        when(cuidadoRepository.findById(id)).thenReturn(Optional.of(existente));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> cuidadoService.atualizar(id, dados));
        assertEquals("A data do cuidado não pode ser no futuro.", ex.getMessage());

        verify(cuidadoRepository, never()).save(any());
    }
}
