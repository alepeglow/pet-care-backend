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
    public List<Pet> listarTodos() {
        return petService.listarTodos();
    }

    // BUSCAR POR ID - GET /pets/{id}
    @GetMapping("/{id}")
    public Pet buscarPorId(@PathVariable Long id) {
        return petService.buscarPorId(id);
    }

    // LISTAR POR TUTOR - GET /tutores/{idTutor}/pets
    @GetMapping("/tutor/{idTutor}")
    public List<Pet> listarPorTutor(@PathVariable Long idTutor) {
        return petService.listarPorTutor(idTutor);
    }

    // CRIAR - POST /pets
    @PostMapping
    public ResponseEntity<Pet> criar(@Valid @RequestBody Pet pet) {
        Pet criado = petService.criar(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    // ATUALIZAR - PUT /pets/{id}
    @PutMapping("/{id}")
    public Pet atualizar(@PathVariable Long id,
                         @Valid @RequestBody Pet pet) {
        return petService.atualizar(id, pet);
    }

    // DELETAR - DELETE /pets/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        petService.deletar(id);
    }

    // ADOTAR PET - POST /pets/{idPet}/adotar?tutorId=ID
    @PostMapping("/{idPet}/adotar")
    public ResponseEntity<Pet> adotar(
            @PathVariable("idPet") Long idPet,
            @RequestParam("tutorId") Long tutorId) {

        Pet petAdotado = petService.adotarPet(idPet, tutorId);
        return ResponseEntity.ok(petAdotado);
    }


}
