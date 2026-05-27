/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.LiniaClient;
import cat.copernic.backend.entity.Lot;
import cat.copernic.backend.enums.EstatLot;
import cat.copernic.backend.repository.ClientRepository;
import cat.copernic.backend.repository.MateriaRepository;
import cat.copernic.backend.repository.ProducteRepository;
import cat.copernic.backend.repository.ProveidorRepository;
import cat.copernic.backend.service.LotService;
import cat.copernic.backend.service.ProduccioLotService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/lots")
public class LotController {

    @Autowired
    private LotService lotService;
    
    @Autowired
    private ProduccioLotService produccioLotService;
    
    @Autowired
    private MateriaRepository materiaRepository;

    @Autowired
    private ProveidorRepository proveidorRepository;
    
    @Autowired
    private ProducteRepository producteRepository;

    @Autowired
    private ClientRepository clientRepository;
    
    @GetMapping
    public String listar(
            @RequestParam(required = false) String numLot,
            @RequestParam(required = false) EstatLot estatLot,
            @RequestParam(required = false) Long materiaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataCaducitat,
            @RequestParam(required = false, defaultValue = "dataCaducitat") String sortField, // <-- NUEVO
            @RequestParam(required = false, defaultValue = "true") boolean asc,
            Model model) {

        List<Lot> lots = lotService.filtrar(numLot, estatLot, materiaId, dataCaducitat);
        
        // Llamamos al nuevo método de ordenación
        lots = lotService.ordenarLots(lots, sortField, asc);

        model.addAttribute("lots", lots);
        model.addAttribute("materies", materiaRepository.findAll());
        model.addAttribute("proveidors", proveidorRepository.findAll());

        model.addAttribute("numLot", numLot);
        model.addAttribute("estatLot", estatLot);
        model.addAttribute("materiaId", materiaId);
        model.addAttribute("dataCaducitat", dataCaducitat);
        
        // Pasamos las variables de ordenación a la vista
        model.addAttribute("sortField", sortField);
        model.addAttribute("asc", asc);

        return "lots";
    }
    
    @PostMapping("/canviar-estat/{id}")
    public String canviarEstat(@PathVariable Long id,
                               @RequestParam(defaultValue = "false") boolean forcar,
                               RedirectAttributes redirectAttributes) {

        Lot lot = lotService.getLotById(id);

        if (lot.getEstatLot() == EstatLot.EN_ESTOC) {

            boolean ok = lotService.obrirLot(id, forcar);

            if (!ok) {
                return "redirect:/lots?conflict=true&lotId=" + id;
            }
        }
        else if (lot.getEstatLot() == EstatLot.OBERT) {
            lotService.acabarLot(id);
        }

        return "redirect:/lots";
    }
    
    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {

        Lot lot = lotService.getLotById(id);

        if (lot.getEstatLot() != EstatLot.EN_ESTOC) {
            return "redirect:/lots?error=no_editable";
        }

        model.addAttribute("lot", lot);
        model.addAttribute("materies", materiaRepository.findAll());
        model.addAttribute("proveidors", proveidorRepository.findAll());
        model.addAttribute("mode", "edit");

        return "modLot";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Lot lot) {

        lotService.editarLot(lot.getId(), lot);

        return "redirect:/lots";
    }
    
    @GetMapping("/{id}/produccions")
    public String produccionsLot(@PathVariable Long id,
                                 @RequestParam(required = false) Long producteId, // Ahora es Long
                                 @RequestParam(required = false) Long clientId,   // Ahora es Long
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataProduccio,
                                 @RequestParam(required = false, defaultValue = "dataProduccio") String sortField,
                                 @RequestParam(required = false, defaultValue = "true") boolean asc,
                                 Model model) {

        Lot lot = lotService.getLotById(id);
        List<LiniaClient> linies = produccioLotService.obtenirProduccioLot(id);

        // 1. Filtrar usando los IDs
        linies = produccioLotService.filtrar(linies, producteId, clientId, dataProduccio);
        
        // 2. Ordenar
        linies = produccioLotService.ordenar(linies, sortField, asc);

        model.addAttribute("lot", lot);
        model.addAttribute("linies", linies);
        
        // Enviamos las listas para rellenar los desplegables
        model.addAttribute("productes", producteRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        
        // Parámetros para mantener el estado de la vista
        model.addAttribute("sortField", sortField);
        model.addAttribute("asc", asc);

        return "produccionsLot";
    }
}
