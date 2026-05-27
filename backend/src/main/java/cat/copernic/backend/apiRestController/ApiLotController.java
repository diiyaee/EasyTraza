/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.apiRestController;

import cat.copernic.backend.DTOs.LotDTO;
import cat.copernic.backend.entity.Lot;
import cat.copernic.backend.enums.EstatLot;
import cat.copernic.backend.service.LotService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author diyae
 */
@RestController
@RequestMapping("/api/lots")
public class ApiLotController {

    @Autowired
    private LotService lotService;

    @GetMapping
    public List<LotDTO> getLots() {

        return lotService.getAllLots()
                .stream()
                .map(LotDTO::new)
                .toList();
    }

    @PostMapping("/canviar-estat/{id}")
    public ResponseEntity<?> canviarEstat(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean forcar
    ) {

        Lot lot = lotService.getLotById(id);

        // CASO 1: abrir
        if (lot.getEstatLot() == EstatLot.EN_ESTOC) {

            boolean ok = lotService.obrirLot(id, forcar);

            if (!ok) {
                return ResponseEntity.status(409)
                        .body("EXISTE_UN_LLOT_OBERT_MATEIXA_MATERIA");
            }

            return ResponseEntity.ok("OBERT");
        }

        // CASO 2: acabar
        if (lot.getEstatLot() == EstatLot.OBERT) {
            lotService.acabarLot(id);
            return ResponseEntity.ok("ACABAT");
        }

        return ResponseEntity.badRequest().body("JA_ACABAT");
    }
}