package br.com.alessandra.petcare.controller;

import br.com.alessandra.petcare.model.Adocao;
import br.com.alessandra.petcare.service.AdocaoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adocoes")
public class AdocaoController {

    private final AdocaoService adocaoService;

    public AdocaoController(AdocaoService adocaoService) {
        this.adocaoService = adocaoService;
    }

    // LISTAR HISTÓRICO DE ADOÇÕES DE UM PET - GET /adocoes/pet/{idPet}
    @GetMapping("/pet/{idPet}")
    public List<Adocao> listarPorPet(@PathVariable Long idPet) {
        return adocaoService.listarPorPet(idPet);
    }

    // LISTAR HISTÓRICO DE ADOÇÕES DE UM TUTOR - GET /adocoes/tutor/{idTutor}
    @GetMapping("/tutor/{idTutor}")
    public List<Adocao> listarPorTutor(@PathVariable Long idTutor) {
        return adocaoService.listarPorTutor(idTutor);
    }
}
