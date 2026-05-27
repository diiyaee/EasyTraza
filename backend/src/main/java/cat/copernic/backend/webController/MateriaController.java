/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.Materia;
import cat.copernic.backend.service.MateriaService;
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
@RequestMapping("/materies")
public class MateriaController {
    
    @Autowired
    private MateriaService materiaService;
    
    @GetMapping
    public String getAllMateries(
            @RequestParam(required = false) String nom,
            @RequestParam(defaultValue = "nom") String sortField,
            @RequestParam(defaultValue = "true") boolean asc,
            Model model) {

        // 1. Filtrar
        List<Materia> materies = materiaService.filtrar(nom);

        // 2. Ordenar
        materies = materiaService.ordenarMateries(materies, sortField, asc);

        // 3. Enviar al modelo
        model.addAttribute("materies", materies);
        model.addAttribute("nom", nom);
        model.addAttribute("asc", asc);

        return "materies";
    }
    
    @GetMapping("/novaMateria")
    public String mostrarFormCrear(Model model) {

        model.addAttribute("materia", new Materia());
        model.addAttribute("mode", "create");

        return "modMateria";
    }
    
    @GetMapping("/modMateria/{id}")
    public String mostrarFormModificar(@PathVariable Long id, Model model) {

        Materia m = materiaService.getMateriaById(id);

        model.addAttribute("materia", m);
        model.addAttribute("mode", "edit");
        
        return "modMateria";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("materia") Materia materia,
                          Model model) {

        try {

            if (materia.getNom() != null && materia.getNom().length() > 100)
                throw new IllegalArgumentException("El nombre no puede tener más de 100 carácteres.");
  
            if (materia.getId() == null) {

                materiaService.crearMateria(materia);
            }

            else {

                materiaService.editarMateria(materia.getId(), materia);
                
            }

            return "redirect:/materies";

        } catch (Exception e) {

            model.addAttribute("missatge", e.getMessage());
            model.addAttribute("materia", materia);

            return "modMateria";
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            materiaService.eliminarMateria(id);
            return "redirect:/materies";

        } catch (ResponseStatusException e) {
            model.addAttribute("missatge", e.getReason());
            model.addAttribute("materies", materiaService.getAllMateries());
            return "materies";

        } catch (Exception e) {
            model.addAttribute("missatge", "Error inesperado al intentar eliminar la materia.");
            model.addAttribute("materies", materiaService.getAllMateries());
            return "materies";
        }
    }
}