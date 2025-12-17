package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.exception.ApiErrorResponse;
import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pets", description = "Cadastro, listagens e operações de adoção/devolução")
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @Operation(summary = "Listar todos os pets")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public ResponseEntity<List<Pet>> listarTodos() {
        return ResponseEntity.ok(petService.listarTodos());
    }

    @Operation(summary = "Listar pets disponíveis")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Pet>> listarDisponiveis() {
        return ResponseEntity.ok(petService.listarDisponiveis());
    }

    @Operation(summary = "Listar pets adotados")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping("/adotados")
    public ResponseEntity<List<Pet>> listarAdotados() {
        return ResponseEntity.ok(petService.listarAdotados());
    }

    @Operation(
            summary = "Buscar pet por ID",
            description = "Retorna o pet pelo id."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    @Operation(
            summary = "Listar pets por tutor",
            description = "Retorna os pets vinculados a um tutor."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Tutor não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/tutor/{idTutor}")
    public ResponseEntity<List<Pet>> listarPorTutor(@PathVariable Long idTutor) {
        return ResponseEntity.ok(petService.listarPorTutor(idTutor));
    }

    @Operation(
            summary = "Criar pet",
            description = "Cria um novo pet. O pet sempre será criado como DISPONIVEL e sem tutor (adoção é via /pets/{idPet}/adotar)."
    )
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação/negócio",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PostMapping
    public ResponseEntity<Pet> criar(@Valid @RequestBody Pet pet) {
        Pet criado = petService.criar(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(
            summary = "Atualizar dados cadastrais do pet",
            description = "Não altera status/tutor (use /adotar ou /devolver)."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação/negócio",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PutMapping("/{id}")
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        return ResponseEntity.ok(petService.atualizar(id, pet));
    }

    @Operation(summary = "Adotar pet")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Regra de negócio violada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pet/Tutor não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PutMapping("/{idPet}/adotar")
    public ResponseEntity<Pet> adotarPet(
            @PathVariable Long idPet,
            @RequestParam("tutorId") Long tutorId) {

        return ResponseEntity.ok(petService.adotarPet(idPet, tutorId));
    }

    @Operation(summary = "Devolver pet")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Regra de negócio violada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PutMapping("/{idPet}/devolver")
    public ResponseEntity<Pet> devolverPet(@PathVariable Long idPet) {
        return ResponseEntity.ok(petService.devolverPet(idPet));
    }

    @Operation(summary = "Deletar pet")
    @ApiResponse(responseCode = "204", description = "Sem conteúdo")
    @ApiResponse(
            responseCode = "400",
            description = "Regra de negócio violada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        petService.deletar(id);
    }
}
