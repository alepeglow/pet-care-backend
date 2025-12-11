package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.model.Cuidado;
import br.com.alessandra.petcare.service.CuidadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuidados")
public class CuidadoController {

    private final CuidadoService cuidadoService;

    public CuidadoController(CuidadoService cuidadoService) {
        this.cuidadoService = cuidadoService;
    }

    // CRIAR CUIDADO - POST /cuidados
    @PostMapping
    public ResponseEntity<Cuidado> criar(@Valid @RequestBody Cuidado cuidado) {
        Cuidado criado = cuidadoService.criar(cuidado);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // LISTAR TODOS - GET /cuidados
    @GetMapping
    public List<Cuidado> listarTodos() {
        return cuidadoService.listarTodos();
    }

    // BUSCAR POR ID - GET /cuidados/{id}
    @GetMapping("/{id}")
    public Cuidado buscarPorId(@PathVariable Long id) {
        return cuidadoService.buscarPorId(id);
    }

    // LISTAR POR PET - GET /cuidados/pet/{idPet}
    @GetMapping("/pet/{idPet}")
    public List<Cuidado> listarPorPet(@PathVariable Long idPet) {
        return cuidadoService.listarPorPet(idPet);
    }

    // ATUALIZAR - PUT /cuidados/{id}
    @PutMapping("/{id}")
    public Cuidado atualizar(@PathVariable Long id,
                             @Valid @RequestBody Cuidado cuidado) {
        return cuidadoService.atualizar(id, cuidado);
    }

    // DELETAR - DELETE /cuidados/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        cuidadoService.deletar(id);
    }
}
