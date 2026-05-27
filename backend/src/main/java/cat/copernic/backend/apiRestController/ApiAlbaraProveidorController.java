/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.apiRestController;

import cat.copernic.backend.DTOs.AlbaraFormDTO;
import cat.copernic.backend.DTOs.AlbaraResponseDTO;
import cat.copernic.backend.DTOs.OcrAlbaraDTO;
import cat.copernic.backend.DTOs.OcrLiniaDTO;
import cat.copernic.backend.entity.AlbaraProveidor;
import cat.copernic.backend.repository.MateriaRepository;
import cat.copernic.backend.repository.ProveidorRepository;
import cat.copernic.backend.service.AlbaraProveidorService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author diyae
 */
@RestController
@RequestMapping("/api/albaranes")
public class ApiAlbaraProveidorController {

    @Autowired
    private AlbaraProveidorService albaraService;

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody AlbaraFormDTO dto) {

        try {

            AlbaraResponseDTO res =
                    albaraService.crearDesdeDTO(dto);

            return ResponseEntity.ok(res);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error",
                            e.getMessage()
                    ));
        }
    }
    
    @PostMapping("/ocr")
    public ResponseEntity<OcrAlbaraDTO> processarOCR(
            @RequestParam("image") MultipartFile image
    ) {
        try {

            AlbaraProveidor albara = albaraService.processarImatge(image);

            // 🔥 CONVERTIR A DTO OCR
            OcrAlbaraDTO dto = new OcrAlbaraDTO();

            dto.setNumAlbara(albara.getNumAlbara());

            if (albara.getProveidor() != null) {
                dto.setProveidor(albara.getProveidor().getNom());
            }

            if (albara.getDataRecepcio() != null) {
                dto.setDataRecepcio(albara.getDataRecepcio().toString());
            }

            List<OcrLiniaDTO> linies = albara.getLinies().stream().map(l -> {

                OcrLiniaDTO lDto = new OcrLiniaDTO();

                if (l.getLot() != null) {
                    lDto.setNumLot(l.getLot().getNumLot());

                    if (l.getLot().getMateria() != null) {
                        lDto.setMateria(l.getLot().getMateria().getNom());
                    }

                    lDto.setDataCaducitat(
                            l.getLot().getDataCaducitat() != null
                                    ? l.getLot().getDataCaducitat().toString()
                                    : null
                    );
                }

                lDto.setQuantitat(l.getQuantitat());
                lDto.setUnitats(l.getUnitats());

                return lDto;

            }).toList();

            dto.setLinies(linies);

            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public List<AlbaraResponseDTO> llistar() {
        return albaraService.getAll()
                .stream()
                .map(AlbaraResponseDTO::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbaraResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
            new AlbaraResponseDTO(albaraService.getById(id))
        );
    }
}