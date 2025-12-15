package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.model.Cuidado;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.TipoCuidado;
import br.com.alessandra.petcare.service.CuidadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuidadoController.class)
@AutoConfigureMockMvc(addFilters = false) // se tiver Security, evita 401/403
class CuidadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CuidadoService cuidadoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cuidado cuidado(Long id, Long petId, TipoCuidado tipo) {
        Pet pet = new Pet();
        pet.setId(petId);

        Cuidado c = new Cuidado();
        c.setId(id);
        c.setTipo(tipo);
        c.setDescricao("desc");
        c.setData(LocalDate.now());
        c.setCusto(new BigDecimal("50.00"));
        c.setPet(pet);
        return c;
    }

    @Test
    void postCriar_deveRetornar201() throws Exception {
        Cuidado retorno = cuidado(10L, 1L, TipoCuidado.BANHO);
        when(cuidadoService.criar(any(Cuidado.class))).thenReturn(retorno);

        String body = """
            {
              "tipo": "BANHO",
              "descricao": "Banho completo",
              "data": "%s",
              "custo": 50.00,
              "pet": {"id": 1}
            }
            """.formatted(LocalDate.now());

        mockMvc.perform(post("/cuidados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));

        verify(cuidadoService).criar(any(Cuidado.class));
    }

    @Test
    void getListarTodos_deveRetornar200() throws Exception {
        when(cuidadoService.listarTodos()).thenReturn(List.of(
                cuidado(1L, 1L, TipoCuidado.BANHO),
                cuidado(2L, 1L, TipoCuidado.TOSA)
        ));

        mockMvc.perform(get("/cuidados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(cuidadoService).listarTodos();
    }

    @Test
    void getBuscarPorId_deveRetornar200() throws Exception {
        when(cuidadoService.buscarPorId(1L)).thenReturn(cuidado(1L, 1L, TipoCuidado.BANHO));

        mockMvc.perform(get("/cuidados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(cuidadoService).buscarPorId(1L);
    }

    @Test
    void getListarPorPet_deveRetornar200() throws Exception {
        when(cuidadoService.listarPorPet(1L)).thenReturn(List.of(
                cuidado(1L, 1L, TipoCuidado.BANHO)
        ));

        mockMvc.perform(get("/cuidados/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(cuidadoService).listarPorPet(1L);
    }

    @Test
    void getListarPorTipo_deveRetornar200() throws Exception {
        when(cuidadoService.listarPorTipo("banho")).thenReturn(List.of(
                cuidado(1L, 1L, TipoCuidado.BANHO)
        ));

        mockMvc.perform(get("/cuidados/tipo/banho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(cuidadoService).listarPorTipo("banho");
    }

    @Test
    void getListarPorPetETipo_deveRetornar200() throws Exception {
        when(cuidadoService.listarPorPetETipo(1L, "banho")).thenReturn(List.of(
                cuidado(1L, 1L, TipoCuidado.BANHO)
        ));

        mockMvc.perform(get("/cuidados/pet/1/tipo/banho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(cuidadoService).listarPorPetETipo(1L, "banho");
    }

    @Test
    void putAtualizar_deveRetornar200() throws Exception {
        Cuidado retorno = cuidado(1L, 1L, TipoCuidado.BANHO);
        when(cuidadoService.atualizar(eq(1L), any(Cuidado.class))).thenReturn(retorno);

        String body = """
            {
              "tipo": "BANHO",
              "descricao": "Atualizado",
              "data": "%s",
              "custo": 60.00,
              "pet": {"id": 1}
            }
            """.formatted(LocalDate.now());

        mockMvc.perform(put("/cuidados/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(cuidadoService).atualizar(eq(1L), any(Cuidado.class));
    }

    @Test
    void deleteDeletar_deveRetornar204() throws Exception {
        doNothing().when(cuidadoService).deletar(1L);

        mockMvc.perform(delete("/cuidados/1"))
                .andExpect(status().isNoContent());

        verify(cuidadoService).deletar(1L);
    }
}
