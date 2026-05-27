/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.DTOs.PerfilDTO;
import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.repository.UsuariRepository;
import cat.copernic.backend.security.UsuariUserDetails;
import cat.copernic.backend.service.UsuariService;
import jakarta.validation.Valid;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuariService usuariService;
    
    @Autowired
    private UsuariRepository usuariRepository;

    @GetMapping
    public String verPerfil(@AuthenticationPrincipal UsuariUserDetails userDetails, Model model) {
        // Sacamos el ID del usuario logueado
        Long miId = userDetails.getUsuari().getId();
        Usuari usuari = usuariRepository.findById(miId).orElseThrow();

        // Rellenamos el DTO para mandarlo a la vista
        PerfilDTO dto = new PerfilDTO();
        dto.setNom(usuari.getNom());
        dto.setCognoms(usuari.getCognoms());
        dto.setEmail(usuari.getEmail());
        
        model.addAttribute("perfilDTO", dto);
        return "perfil";
    }

    @PostMapping("/guardar")
    public String guardarPerfil(@Valid @ModelAttribute("perfilDTO") PerfilDTO perfilDTO,
                                BindingResult result,
                                @RequestParam(value = "archivoFoto", required = false) MultipartFile archivoFoto, // <-- AÑADIDO
                                @AuthenticationPrincipal UsuariUserDetails userDetails,
                                RedirectAttributes redirect,
                                Model model) {

        if (result.hasErrors()) {
            return "perfil";
        }

        try {
            // --- NUEVA LÓGICA DE LA FOTO ---
            if (archivoFoto != null && !archivoFoto.isEmpty()) {
                if (!archivoFoto.getContentType().startsWith("image/")) {
                    throw new RuntimeException("El archivo debe ser una imagen.");
                }

                // Convertimos la imagen a Base64 y se la pasamos al DTO
                String base64Image = Base64.getEncoder().encodeToString(archivoFoto.getBytes());
                perfilDTO.setFotoPerfil("data:" + archivoFoto.getContentType() + ";base64," + base64Image);
            }
            // -------------------------------

            Long miId = userDetails.getUsuari().getId();
            usuariService.actualizarMiPerfil(miId, perfilDTO);

            redirect.addFlashAttribute("ok", "Tu perfil se ha actualizado correctamente.");

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "perfil";
        }

        return "redirect:/perfil";
    }
}