package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.model.Pet;
import br.com.alessandra.petcare.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // LISTAR TODOS - GET /pets
    @GetMapping
    public ResponseEntity<List<Pet>> listarTodos() {
        return ResponseEntity.ok(petService.listarTodos());
    }

    // LISTAR APENAS PETS DISPONÍVEIS - GET /pets/disponiveis
    @GetMapping("/disponiveis")
    public ResponseEntity<List<Pet>> listarDisponiveis() {
        return ResponseEntity.ok(petService.listarDisponiveis());
    }

    // LISTAR APENAS PETS ADOTADOS - GET /pets/adotados
    @GetMapping("/adotados")
    public ResponseEntity<List<Pet>> listarAdotados() {
        return ResponseEntity.ok(petService.listarAdotados());
    }

    // BUSCAR POR ID - GET /pets/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    // LISTAR POR TUTOR - GET /pets/tutor/{idTutor}
    @GetMapping("/tutor/{idTutor}")
    public ResponseEntity<List<Pet>> listarPorTutor(@PathVariable Long idTutor) {
        return ResponseEntity.ok(petService.listarPorTutor(idTutor));
    }

    // CRIAR - POST /pets
    @PostMapping
    public ResponseEntity<Pet> criar(@Valid @RequestBody Pet pet) {
        Pet criado = petService.criar(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // ATUALIZAR (cadastro) - PUT /pets/{id}
    // Regras de negócio: status e tutor NÃO mudam aqui (só via /adotar e /devolver)
    @PutMapping("/{id}")
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        return ResponseEntity.ok(petService.atualizar(id, pet));
    }

    // ADOTAR PET - PUT /pets/{idPet}/adotar?tutorId=ID
    @PutMapping("/{idPet}/adotar")
    public ResponseEntity<Pet> adotarPet(
            @PathVariable Long idPet,
            @RequestParam("tutorId") Long tutorId) {

        return ResponseEntity.ok(petService.adotarPet(idPet, tutorId));
    }

    // DEVOLVER PET - PUT /pets/{idPet}/devolver
    @PutMapping("/{idPet}/devolver")
    public ResponseEntity<Pet> devolverPet(@PathVariable Long idPet) {
        return ResponseEntity.ok(petService.devolverPet(idPet));
    }

    // DELETAR - DELETE /pets/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        petService.deletar(id);
    }
}
