package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.exception.GlobalExceptionHandler;
import br.com.alessandra.petcare.exception.NotFoundException;
import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.service.AdocaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdocaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdocaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdocaoService adocaoService;

    @Test
    void getListarPorPet_deveRetornar200() throws Exception {
        Adocao a1 = new Adocao(); a1.setId(1L);
        Adocao a2 = new Adocao(); a2.setId(2L);

        when(adocaoService.listarPorPet(1L)).thenReturn(List.of(a1, a2));

        mockMvc.perform(get("/adocoes/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(adocaoService).listarPorPet(1L);
        verifyNoMoreInteractions(adocaoService);
    }

    @Test
    void getListarPorPet_quandoNaoEncontrado_deveRetornar404() throws Exception {
        when(adocaoService.listarPorPet(99L))
                .thenThrow(new NotFoundException("Pet não encontrado com id: 99"));

        mockMvc.perform(get("/adocoes/pet/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Pet não encontrado com id: 99"));

        verify(adocaoService).listarPorPet(99L);
        verifyNoMoreInteractions(adocaoService);
    }

    @Test
    void getListarPorTutor_deveRetornar200() throws Exception {
        Adocao a1 = new Adocao(); a1.setId(10L);

        when(adocaoService.listarPorTutor(7L)).thenReturn(List.of(a1));

        mockMvc.perform(get("/adocoes/tutor/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(10));

        verify(adocaoService).listarPorTutor(7L);
        verifyNoMoreInteractions(adocaoService);
    }

    @Test
    void getListarPorTutor_quandoNaoEncontrado_deveRetornar404() throws Exception {
        when(adocaoService.listarPorTutor(999L))
                .thenThrow(new NotFoundException("Tutor não encontrado com id: 999"));

        mockMvc.perform(get("/adocoes/tutor/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Tutor não encontrado com id: 999"));

        verify(adocaoService).listarPorTutor(999L);
        verifyNoMoreInteractions(adocaoService);
    }
}
