package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.exception.ApiErrorResponse;
import br.com.alessandra.petcare.model.Cuidado;
import br.com.alessandra.petcare.service.CuidadoService;
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

@Tag(name = "Cuidados", description = "Registro e consulta de cuidados (banho, tosa, vacina, etc.)")
@RestController
@RequestMapping("/cuidados")
public class CuidadoController {

    private final CuidadoService cuidadoService;

    public CuidadoController(CuidadoService cuidadoService) {
        this.cuidadoService = cuidadoService;
    }

    @Operation(summary = "Criar cuidado")
    @ApiResponse(responseCode = "201", description = "Criado")
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
    @PostMapping
    public ResponseEntity<Cuidado> criar(@Valid @RequestBody Cuidado cuidado) {
        Cuidado criado = cuidadoService.criar(cuidado);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(summary = "Listar todos os cuidados")
    @ApiResponse(responseCode = "200", description = "OK")
    @GetMapping
    public ResponseEntity<List<Cuidado>> listarTodos() {
        return ResponseEntity.ok(cuidadoService.listarTodos());
    }

    @Operation(summary = "Buscar cuidado por ID")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "404",
            description = "Cuidado não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/{id}")
    public ResponseEntity<Cuidado> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cuidadoService.buscarPorId(id));
    }

    @Operation(summary = "Listar cuidados por pet")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "404",
            description = "Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/pet/{idPet}")
    public ResponseEntity<List<Cuidado>> listarPorPet(@PathVariable Long idPet) {
        return ResponseEntity.ok(cuidadoService.listarPorPet(idPet));
    }

    @Operation(summary = "Atualizar cuidado")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Erro de validação/negócio",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Cuidado/Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @PutMapping("/{id}")
    public ResponseEntity<Cuidado> atualizar(@PathVariable Long id, @Valid @RequestBody Cuidado cuidado) {
        return ResponseEntity.ok(cuidadoService.atualizar(id, cuidado));
    }

    @Operation(summary = "Deletar cuidado")
    @ApiResponse(responseCode = "204", description = "Sem conteúdo")
    @ApiResponse(
            responseCode = "404",
            description = "Cuidado não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cuidadoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar cuidados por tipo")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Tipo inválido",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Cuidado>> listarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(cuidadoService.listarPorTipo(tipo));
    }

    @Operation(summary = "Listar cuidados por pet e tipo")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "400",
            description = "Tipo inválido",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/pet/{idPet}/tipo/{tipo}")
    public ResponseEntity<List<Cuidado>> listarPorPetETipo(@PathVariable Long idPet, @PathVariable String tipo) {
        return ResponseEntity.ok(cuidadoService.listarPorPetETipo(idPet, tipo));
    }
}
