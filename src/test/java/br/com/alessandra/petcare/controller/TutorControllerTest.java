package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.service.TutorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TutorController.class)
@AutoConfigureMockMvc(addFilters = false)
class TutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TutorService tutorService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tutor tutor(Long id, String nome, String email) {
        Tutor t = new Tutor();
        t.setId(id);
        t.setNome(nome);
        t.setEmail(email);
        t.setTelefone("99999-9999");
        t.setEndereco("Rua X, 123");
        return t;
    }

    @Test
    void listarTodos_deveRetornar200ELista() throws Exception {
        when(tutorService.listarTodos()).thenReturn(List.of(
                tutor(1L, "Ana", "ana@email.com"),
                tutor(2L, "Bia", "bia@email.com")
        ));

        mockMvc.perform(get("/tutores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(tutorService).listarTodos();
    }

    @Test
    void buscarPorId_deveRetornar200() throws Exception {
        when(tutorService.buscarPorId(10L)).thenReturn(tutor(10L, "Carlos", "carlos@email.com"));

        mockMvc.perform(get("/tutores/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@email.com"));

        verify(tutorService).buscarPorId(10L);
    }

    @Test
    void criar_deveRetornar201() throws Exception {
        Tutor retorno = tutor(5L, "Duda", "duda@email.com");
        when(tutorService.criar(any(Tutor.class))).thenReturn(retorno);

        String body = """
            {
              "nome": "Duda",
              "telefone": "99999-0000",
              "email": "duda@email.com",
              "endereco": "Rua Y, 50"
            }
            """;

        mockMvc.perform(post("/tutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nome").value("Duda"))
                .andExpect(jsonPath("$.email").value("duda@email.com"));

        verify(tutorService).criar(any(Tutor.class));
    }

    @Test
    void atualizar_deveRetornar200() throws Exception {
        Tutor retorno = tutor(7L, "Eva", "eva@email.com");
        when(tutorService.atualizar(eq(7L), any(Tutor.class))).thenReturn(retorno);

        String body = """
            {
              "nome": "Eva",
              "telefone": "99999-1111",
              "email": "eva@email.com",
              "endereco": "Rua Z, 9"
            }
            """;

        mockMvc.perform(put("/tutores/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.nome").value("Eva"))
                .andExpect(jsonPath("$.email").value("eva@email.com"));

        verify(tutorService).atualizar(eq(7L), any(Tutor.class));
    }

    @Test
    void deletar_deveRetornar204() throws Exception {
        doNothing().when(tutorService).deletar(3L);

        mockMvc.perform(delete("/tutores/3"))
                .andExpect(status().isNoContent());

        verify(tutorService).deletar(3L);
    }

    // ============================
    // Validações (@Valid no body)
    // ============================

    @Test
    void criar_deveRetornar400QuandoNomeVazio() throws Exception {
        String body = """
            {
              "nome": "",
              "telefone": "99999-0000",
              "email": "ok@email.com",
              "endereco": "Rua X"
            }
            """;

        mockMvc.perform(post("/tutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(tutorService, never()).criar(any());
    }

    @Test
    void criar_deveRetornar400QuandoEmailInvalido() throws Exception {
        String body = """
            {
              "nome": "Fulano",
              "telefone": "99999-0000",
              "email": "email-invalido",
              "endereco": "Rua X"
            }
            """;

        mockMvc.perform(post("/tutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(tutorService, never()).criar(any());
    }
}

