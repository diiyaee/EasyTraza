/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.PasswordResetToken;
import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.repository.PasswordResetTokenRepository;
import cat.copernic.backend.repository.UsuariRepository;
import cat.copernic.backend.service.EmailService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/recuperar-password")
public class PasswordResetController {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Muestra la pantalla para pedir el correo
    @GetMapping
    public String mostrarFormularioEmail() {
        return "recuperarSolicitud";
    }

    // 2. Procesa el correo y envía el email con el enlace
    @PostMapping
    @Transactional
    public String procesarSolicitudEmail(@RequestParam("email") String email, Model model) {
        Optional<Usuari> userOpt = usuariRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            // Por seguridad, en el mundo real se suele decir que se ha enviado igualmente 
            // para no dar pistas de qué correos existen, pero para tu proyecto ponemos el error directo.
            model.addAttribute("error", "No existe ningún usuario registrado con este correo electrónico.");
            return "recuperarSolicitud";
        }

        Usuari usuari = userOpt.get();

        // Limpiamos tokens viejos que pudiera tener este usuario para no saturar la BD
        tokenRepository.deleteByQueryUsuario(usuari);

        // Generamos un token totalmente aleatorio y único (ej: "4a2b-8c3d-...")
        String tokenSeguro = UUID.randomUUID().toString();

        // Creamos el registro del token con una validez de 15 minutos
        PasswordResetToken miToken = new PasswordResetToken(tokenSeguro, usuari, 15);
        tokenRepository.save(miToken);

        // Construimos el enlace hacia nuestro servidor local
        String enlace = "http://localhost:8080/recuperar-password/cambiar?token=" + tokenSeguro;

        try {
            emailService.enviarEmailRecuperacion(usuari.getEmail(), enlace);
            model.addAttribute("ok", "Te hemos enviado un correo con las instrucciones para restablecer tu contraseña. Revisa tu bandeja de entrada.");
        } catch (Exception e) {
            model.addAttribute("error", "Error al enviar el correo: " + e.getMessage());
        }

        return "recuperarSolicitud";
    }

    // 3. Procesa el clic en el enlace del correo electrónico
    @GetMapping("/cambiar")
    public String mostrarFormularioCambio(@RequestParam("token") String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().estaCaducado()) {
            model.addAttribute("errorToken", "El enlace de recuperación no es válido o ha caducado. Por favor, solicita uno nuevo.");
            return "recuperarSolicitud"; // Lo mandamos de vuelta al inicio
        }

        // Si el token es válido, le pasamos el token al HTML de manera oculta
        model.addAttribute("token", token);
        return "recuperarNueva";
    }

    // 4. Procesa la nueva contraseña escrita por el usuario
    @PostMapping("/cambiar")
    @Transactional
    public String procesarNuevaContrasenya(@RequestParam("token") String token,
                                           @RequestParam("password") String nuevaPassword,
                                           RedirectAttributes redirect,
                                           Model model) {
        
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty() || tokenOpt.get().estaCaducado()) {
            model.addAttribute("errorToken", "El token ha caducado durante el proceso. Solicita uno nuevo.");
            return "recuperarSolicitud";
        }

        if (nuevaPassword.length() < 6) {
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            model.addAttribute("token", token);
            return "recuperarNueva";
        }

        // Recuperamos el usuario y modificamos su contraseña encriptándola
        PasswordResetToken resetToken = tokenOpt.get();
        Usuari usuari = resetToken.getQueryUsuario();
        usuari.setContrasenya(passwordEncoder.encode(nuevaPassword));
        usuariRepository.save(usuari);

        // Consumimos/borramos el token para que nadie pueda volver a usar el mismo enlace
        tokenRepository.delete(resetToken);

        redirect.addFlashAttribute("logout", "¡Tu contraseña se ha restablecido con éxito! Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}