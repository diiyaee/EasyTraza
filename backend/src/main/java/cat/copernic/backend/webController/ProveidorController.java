/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.Proveidor;
import cat.copernic.backend.service.ProveidorService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
@RequestMapping("/proveidors")
public class ProveidorController {
    
    @Autowired
    private ProveidorService proveidorService;
    
    @GetMapping
    public String getAllProveidors(Model model,
                                   @RequestParam(required = false) String nom,
                                   @RequestParam(required = false) String nif,
                                   @RequestParam(required = false) String telefon,
                                   @RequestParam(required = false) String adreca,
                                   @RequestParam(required = false, defaultValue = "nom") String sortField,
                                   @RequestParam(required = false, defaultValue = "true") boolean asc) {

        // 1. Filtrar
        List<Proveidor> proveidors = proveidorService.filtrar(nom, nif, telefon, adreca);

        // 2. Ordenar
        proveidors = proveidorService.ordenarProveidors(proveidors, sortField, asc);

        // 3. Pasar a la vista
        model.addAttribute("proveidors", proveidors);

        // Mantener los valores en el formulario de búsqueda
        model.addAttribute("nom", nom);
        model.addAttribute("nif", nif);
        model.addAttribute("telefon", telefon);
        model.addAttribute("adreca", adreca);
        model.addAttribute("sortField", sortField);
        model.addAttribute("asc", asc);

        return "proveidors";
    }
    
    @GetMapping("/nouProveidor")
    public String mostrarFormCrear(Model model) {

        model.addAttribute("proveidor", new Proveidor());
        model.addAttribute("mode", "create");

        return "modProveidor";
    }
    
    @GetMapping("/modProveidor/{id}")
    public String mostrarFormModificar(@PathVariable Long id, Model model) {

        Proveidor p = proveidorService.getProveidorById(id);

        model.addAttribute("proveidor", p);
        model.addAttribute("mode", "edit");
        
        return "modProveidor";
    }
    
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("proveidor") Proveidor proveidor,
                          BindingResult result, 
                          Model model) {

        try {
            if (proveidor.getNif() != null &&
                !proveidor.getNif().matches("(^[0-9]{8}[A-HJ-NP-TV-Z]$)|(^[A-HJ-NP-TV-Z][0-9]{7,8}[0-9A-J]$)")) {

                throw new IllegalArgumentException("El NIF/DNI no es válido.");
            }
            
            if (proveidor.getNif() != null && proveidor.getNif().trim().length() > 9)
                throw new IllegalArgumentException("El NIF no puede tener más de 9 caracteres.");

            if (proveidor.getNom() != null && proveidor.getNom().length() > 100)
                throw new IllegalArgumentException("El nombre no puede tener más de 100 caracteres.");

            if (proveidor.getTelefon() != null && !proveidor.getTelefon().matches("^[0-9]{9}$"))
                throw new IllegalArgumentException("El teléfono debe tener 9 dígitos.");

            if (proveidor.getAdreca() != null && proveidor.getAdreca().length() > 200)
                throw new IllegalArgumentException("La dirección no puede tener más de 200 caracteres.");

            if (proveidor.getObservacions() != null && proveidor.getObservacions().length() > 500)
                throw new IllegalArgumentException("Las observaciones no pueden superar los 500 caracteres.");
            
            if (proveidor.getId() == null) {

                proveidorService.crearProveidor(proveidor);
            }

            else {

                proveidorService.editarProveidor(proveidor.getId(), proveidor);
                
            }

            return "redirect:/proveidors";

        } catch (Exception e) {

            model.addAttribute("missatge", e.getMessage());
            model.addAttribute("proveidor", proveidor);

            return "modProveidor";
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            proveidorService.eliminarProveidor(id);
            return "redirect:/proveidors"; 

        } catch (ResponseStatusException e) {
            model.addAttribute("missatge", e.getReason());
            model.addAttribute("proveidors", proveidorService.getAllProveidors()); 
            return "proveidors"; 

        } catch (Exception e) {
            model.addAttribute("missatge", "Error inesperado al intentar eliminar el proveedor.");
            model.addAttribute("proveidors", proveidorService.getAllProveidors());
            return "proveidors";
        }
    }
}