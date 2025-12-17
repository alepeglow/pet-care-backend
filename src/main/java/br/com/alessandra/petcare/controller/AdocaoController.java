package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.exception.ApiErrorResponse;
import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.service.AdocaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Adoções", description = "Histórico de adoções por pet e por tutor")
@RestController
@RequestMapping("/adocoes")
public class AdocaoController {

    private final AdocaoService adocaoService;

    public AdocaoController(AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
    }

    @Operation(
            summary = "Listar histórico de adoções por pet",
            description = "Retorna o histórico de adoções do pet (mais recente primeiro)."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "404",
            description = "Pet não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/pet/{idPet}")
    public ResponseEntity<List<Adocao>> listarPorPet(@PathVariable Long idPet) {
        return ResponseEntity.ok(adocaoService.listarPorPet(idPet));
    }

    @Operation(
            summary = "Listar histórico de adoções por tutor",
            description = "Retorna o histórico de adoções do tutor (mais recente primeiro)."
    )
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(
            responseCode = "404",
            description = "Tutor não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
    )
    @GetMapping("/tutor/{idTutor}")
    public ResponseEntity<List<Adocao>> listarPorTutor(@PathVariable Long idTutor) {
        return ResponseEntity.ok(adocaoService.listarPorTutor(idTutor));
    }
}
