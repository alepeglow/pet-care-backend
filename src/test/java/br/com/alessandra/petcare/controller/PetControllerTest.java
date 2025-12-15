package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.model.StatusPet;
import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.service.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listarTodos_deveRetornar200ELista() throws Exception {
        Pet p1 = new Pet(); p1.setId(1L);
        Pet p2 = new Pet(); p2.setId(2L);

        when(petService.listarTodos()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(petService).listarTodos();
        verifyNoMoreInteractions(petService);
    }

    @Test
    void listarDisponiveis_deveRetornar200() throws Exception {
        Pet p1 = new Pet(); p1.setId(1L); p1.setStatus(StatusPet.DISPONIVEL);

        when(petService.listarDisponiveis()).thenReturn(List.of(p1));

        mockMvc.perform(get("/pets/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("DISPONIVEL"));

        verify(petService).listarDisponiveis();
        verifyNoMoreInteractions(petService);
    }

    @Test
    void listarAdotados_deveRetornar200() throws Exception {
        Pet p1 = new Pet(); p1.setId(3L); p1.setStatus(StatusPet.ADOTADO);

        when(petService.listarAdotados()).thenReturn(List.of(p1));

        mockMvc.perform(get("/pets/adotados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("ADOTADO"));

        verify(petService).listarAdotados();
        verifyNoMoreInteractions(petService);
    }

    @Test
    void buscarPorId_deveRetornar200() throws Exception {
        Pet pet = new Pet();
        pet.setId(10L);
        pet.setStatus(StatusPet.DISPONIVEL);

        when(petService.buscarPorId(10L)).thenReturn(pet);

        mockMvc.perform(get("/pets/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));

        verify(petService).buscarPorId(10L);
        verifyNoMoreInteractions(petService);
    }

    @Test
    void listarPorTutor_deveRetornar200ELista() throws Exception {
        Long idTutor = 10L;

        Pet p1 = new Pet(); p1.setId(1L);
        Pet p2 = new Pet(); p2.setId(2L);

        when(petService.listarPorTutor(idTutor)).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/pets/tutor/{idTutor}", idTutor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(petService).listarPorTutor(idTutor);
        verifyNoMoreInteractions(petService);
    }

    @Test
    void criar_deveRetornar201() throws Exception {
        // Ajusta os campos abaixo caso teu Pet tenha @NotNull/@NotBlank neles
        Pet body = new Pet();
        body.setNome("Bolt");
        body.setEspecie("CACHORRO");
        body.setRaca("Vira-lata");
        body.setIdade(2);
        body.setStatus(StatusPet.DISPONIVEL);
        body.setDataEntrada(LocalDate.now());

        Pet criado = new Pet();
        criado.setId(100L);
        criado.setNome(body.getNome());
        criado.setEspecie(body.getEspecie());
        criado.setRaca(body.getRaca());
        criado.setIdade(body.getIdade());
        criado.setStatus(body.getStatus());
        criado.setDataEntrada(body.getDataEntrada());

        when(petService.criar(any(Pet.class))).thenReturn(criado);

        mockMvc.perform(post("/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.nome").value("Bolt"));

        verify(petService).criar(any(Pet.class));
        verifyNoMoreInteractions(petService);
    }

    @Test
    void atualizar_deveRetornar200() throws Exception {
        Long id = 5L;

        Pet body = new Pet();
        body.setNome("Mel");
        body.setEspecie("GATO");
        body.setRaca("SRD");
        body.setIdade(3);
        body.setStatus(StatusPet.DISPONIVEL); // <-- ADICIONA ISSO
        body.setDataEntrada(LocalDate.now());


        Pet atualizado = new Pet();
        atualizado.setId(id);
        atualizado.setNome(body.getNome());
        atualizado.setEspecie(body.getEspecie());
        atualizado.setRaca(body.getRaca());
        atualizado.setIdade(body.getIdade());
        atualizado.setDataEntrada(body.getDataEntrada());
        atualizado.setStatus(StatusPet.DISPONIVEL);

        when(petService.atualizar(eq(id), any(Pet.class))).thenReturn(atualizado);

        mockMvc.perform(put("/pets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nome").value("Mel"));

        verify(petService).atualizar(eq(id), any(Pet.class));
        verifyNoMoreInteractions(petService);
    }

    @Test
    void adotarPet_deveRetornar200EJsonDoPet() throws Exception {
        Long idPet = 1L;
        Long tutorId = 10L;

        Tutor tutor = new Tutor();
        tutor.setId(tutorId);

        Pet petAdotado = new Pet();
        petAdotado.setId(idPet);
        petAdotado.setStatus(StatusPet.ADOTADO);
        petAdotado.setTutor(tutor);

        when(petService.adotarPet(idPet, tutorId)).thenReturn(petAdotado);

        mockMvc.perform(put("/pets/{idPet}/adotar", idPet)
                        .param("tutorId", tutorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ADOTADO"))
                .andExpect(jsonPath("$.tutor.id").value(10));

        verify(petService).adotarPet(idPet, tutorId);
        verifyNoMoreInteractions(petService);
    }

    @Test
    void devolverPet_deveRetornar200EJsonDoPet() throws Exception {
        Long idPet = 1L;

        Pet petDevolvido = new Pet();
        petDevolvido.setId(idPet);
        petDevolvido.setStatus(StatusPet.DISPONIVEL);
        petDevolvido.setTutor(null);

        when(petService.devolverPet(idPet)).thenReturn(petDevolvido);

        mockMvc.perform(put("/pets/{idPet}/devolver", idPet))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));

        verify(petService).devolverPet(idPet);
        verifyNoMoreInteractions(petService);
    }

    @Test
    void deletar_deveRetornar204() throws Exception {
        doNothing().when(petService).deletar(5L);

        mockMvc.perform(delete("/pets/5"))
                .andExpect(status().isNoContent());

        verify(petService).deletar(5L);
        verifyNoMoreInteractions(petService);
    }
}
