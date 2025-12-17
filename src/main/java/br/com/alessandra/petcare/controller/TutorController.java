package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.exception.ApiErrorResponse;
import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.service.TutorService;
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

@Tag(name = "Tutores", description = "Cadastro e manutenção de tutores")
@RestController
@RequestMapping("/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @Operation(summary = "Listar todos os tutores")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public ResponseEntity<List<Tutor>> listarTodos() {
        return ResponseEntity.ok(tutorService.listarTodos());
    }

    @Operation(summary = "Buscar tutor por ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "404",
            description = "Tutor não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<Tutor> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.buscarPorId(id));
    }

    @Operation(summary = "Criar tutor")
    @ApiResponse(responseCode = "201", description = "Criado")
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação/negócio",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PostMapping
    public ResponseEntity<Tutor> criar(@Valid @RequestBody Tutor tutor) {
        Tutor criado = tutorService.criar(tutor);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(summary = "Atualizar tutor")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação/negócio",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Tutor não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PutMapping("/{id}")
    public ResponseEntity<Tutor> atualizar(@PathVariable Long id, @Valid @RequestBody Tutor tutor) {
        return ResponseEntity.ok(tutorService.atualizar(id, tutor));
    }

    @Operation(summary = "Deletar tutor")
    @ApiResponse(responseCode = "204", description = "Sem conteúdo")
    @ApiResponse(
            responseCode = "400",
            description = "Regra de negócio violada (ex: tutor possui pets)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Tutor não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tutorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
