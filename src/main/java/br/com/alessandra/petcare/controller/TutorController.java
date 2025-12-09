package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.model.Tutor;
import br.com.alessandra.petcare.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    // LISTAR TODOS - GET /tutores
    @GetMapping
    public List<Tutor> listarTodos() {
        return tutorService.listarTodos();
    }

    // BUSCAR POR ID - GET /tutores/{id}
    @GetMapping("/{id}")
    public Tutor buscarPorId(@PathVariable Long id) {
        return tutorService.buscarPorId(id);
    }

    // CRIAR - POST /tutores
    @PostMapping
    public ResponseEntity<Tutor> criar(@Valid @RequestBody Tutor tutor) {
        Tutor criado = tutorService.criar(tutor);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // ATUALIZAR - PUT /tutores/{id}
    @PutMapping("/{id}")
    public Tutor atualizar(@PathVariable Long id,
                           @Valid @RequestBody Tutor tutor) {
        return tutorService.atualizar(id, tutor);
    }

    // DELETAR - DELETE /tutores/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        tutorService.deletar(id);
    }



}
