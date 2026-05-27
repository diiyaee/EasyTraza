/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.Producte;
import cat.copernic.backend.service.ProducteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/productes")
public class ProducteController {
    
    @Autowired
    private ProducteService producteService;
    
    @GetMapping
    public String getAllProductes(
            @RequestParam(required = false) String nom,
            @RequestParam(defaultValue = "nom") String sortField,
            @RequestParam(defaultValue = "true") boolean asc,
            Model model) {

        List<Producte> productes = producteService.filtrar(nom);
        productes = producteService.ordenarProductes(productes, sortField, asc);

        model.addAttribute("productes", productes);
        model.addAttribute("sortField", sortField);
        model.addAttribute("asc", asc);

        return "productes";
    }
    
    @GetMapping("/nouProducte")
    public String mostrarFormCrear(Model model) {

        model.addAttribute("producte", new Producte());
        model.addAttribute("mode", "create");

        return "modProducte";
    }
    
    @GetMapping("/modProducte/{id}")
    public String mostrarFormModificar(@PathVariable Long id, Model model) {

        Producte p = producteService.getProducteById(id);

        model.addAttribute("producte", p);
        model.addAttribute("mode", "edit");
        
        return "modProducte";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("producte") Producte producte,
                          Model model) {

        try {

            if (producte.getNom() != null && producte.getNom().length() > 100)
                throw new IllegalArgumentException("El nombre no puede tener más de 100 carácteres.");
  
            if (producte.getId() == null) {

                producteService.crearProducte(producte);
            }

            else {

                producteService.editarProducte(producte.getId(), producte);
                
            }

            return "redirect:/productes";

        } catch (Exception e) {

            model.addAttribute("missatge", e.getMessage());
            model.addAttribute("producte", producte);

            return "modProducte";
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            producteService.eliminarProducte(id);
            return "redirect:/productes";

        } catch (ResponseStatusException e) {
            model.addAttribute("missatge", e.getReason());
            model.addAttribute("productes", producteService.getAllProductes()); 
            return "productes";

        } catch (Exception e) {
            model.addAttribute("missatge", "Error inesperado al intentar eliminar el producto.");
            model.addAttribute("productes", producteService.getAllProductes());
            return "productes";
        }
    }
}