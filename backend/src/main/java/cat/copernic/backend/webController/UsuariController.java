/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.service.UsuariService;
import java.util.Base64;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/usuaris")
public class UsuariController {
    
    @Autowired
    private UsuariService usuariService;
    
    @GetMapping
    public String getAllUsuaris(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "true") boolean asc,
            Model model) {

        // 1. Filtrar
        List<Usuari> usuaris = usuariService.filtrar(query);

        // 2. Ordenar
        usuaris = usuariService.ordenarUsuaris(usuaris, asc);

        // 3. Enviar al modelo
        model.addAttribute("usuaris", usuaris);
        model.addAttribute("query", query);
        model.addAttribute("asc", asc);

        return "usuaris";
    }
    
    @GetMapping("/nouUsuari")
    public String mostrarFormCrear(Model model) {

        model.addAttribute("usuari", new Usuari());
        model.addAttribute("mode", "create");

        return "modUsuari";
    }
    
    @GetMapping("/modUsuari/{id}")
    public String mostrarFormModificar(@PathVariable Long id, Model model) {

        Usuari u = usuariService.getUsuariById(id);

        model.addAttribute("usuari", u);
        model.addAttribute("mode", "edit");
        
        return "modUsuari";
    }
    
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuari") Usuari usuari,
                          @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto,
                          Model model) {

        try {

            if (usuari.getNom() != null && usuari.getNom().length() > 50)
                throw new IllegalArgumentException("El nombre no puede tener más de 50 carácteres.");

            if (usuari.getCognoms() != null && usuari.getCognoms().length() > 100)
                throw new IllegalArgumentException("Los apellidos no pueden tener más de 100 carácteres.");
            
            if (usuari.getNom() != null && !usuari.getNom().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+"))
                throw new IllegalArgumentException("El nombre solo puede contener letras.");

            if (usuari.getCognoms() != null && !usuari.getCognoms().matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+"))
                throw new IllegalArgumentException("Los apellidos solo pueden contener letras.");
            
            if (usuari.getEmail() != null && !usuari.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
                throw new IllegalArgumentException("El email no tiene un formato válido.");
            
            if (usuari.getContrasenya() != null && !usuari.getContrasenya().isEmpty()) {

                if (usuari.getContrasenya().length() < 6)
                    throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
            }
            
            if (archivoFoto != null && !archivoFoto.isEmpty()) {
                if (!archivoFoto.getContentType().startsWith("image/")) {
                    throw new IllegalArgumentException("El archivo debe ser una imagen.");
                }

                String base64Image = Base64.getEncoder().encodeToString(archivoFoto.getBytes());
                usuari.setFotoPerfil("data:" + archivoFoto.getContentType() + ";base64," + base64Image);
            }
            
            if (usuari.getId() == null) {

                usuariService.crearUsuari(usuari);
            }

            else {

                usuariService.editarUsuari(usuari.getId(), usuari);
                
            }

            return "redirect:/usuaris";

        } catch (Exception e) {

            model.addAttribute("missatge", e.getMessage());
            model.addAttribute("usuari", usuari);

            return "modUsuari";
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            usuariService.eliminarUsuari(id);
            return "redirect:/usuaris";

        } catch (ResponseStatusException e) {
            model.addAttribute("missatge", e.getReason());
            model.addAttribute("usuaris", usuariService.getAllUsuaris()); 
            return "usuaris";

        } catch (Exception e) {
            model.addAttribute("missatge", "Error inesperado al intentar eliminar el usuario.");
            model.addAttribute("usuaris", usuariService.getAllUsuaris());
            return "usuaris";
        }
    }
}
