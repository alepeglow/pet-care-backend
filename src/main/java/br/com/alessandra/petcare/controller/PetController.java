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

    // LISTAR APENAS PETS DISPONÍVEIS - GET /pets/disponiveis
    @GetMapping("/disponiveis")
    public List<Pet> listarDisponiveis() {
        return petService.listarDisponiveis();
    }

    // LISTAR APENAS PETS ADOTADOS - GET /pets/adotados
    @GetMapping("/adotados")
    public List<Pet> listarAdotados() {
        return petService.listarAdotados();
    }

    // BUSCAR POR ID - GET /pets/{id}
    @GetMapping("/{id}")
    public Pet buscarPorId(@PathVariable Long id) {
        return petService.buscarPorId(id);
    }

    // LISTAR POR TUTOR - GET /pets/tutor/{idTutor}
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

    // ADOTAR PET - PUT /pets/{idPet}/adotar?tutorId=ID
    @PutMapping("/{idPet}/adotar")
    public ResponseEntity<Pet> adotarPet(
            @PathVariable Long idPet,
            @RequestParam Long tutorId) {

        Pet petAdotado = petService.adotarPet(idPet, tutorId);
        return ResponseEntity.ok(petAdotado);
    }

    // DEVOLVER PET - PUT /pets/{idPet}/devolver
    @PutMapping("/{idPet}/devolver")
    public ResponseEntity<Pet> devolverPet(@PathVariable Long idPet) {
        Pet petDevolvido = petService.devolverPet(idPet);
        return ResponseEntity.ok(petDevolvido);
    }

    // DELETAR - DELETE /pets/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        petService.deletar(id);
    }
}
