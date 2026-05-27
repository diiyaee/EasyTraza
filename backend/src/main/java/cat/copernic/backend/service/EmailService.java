/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author diyae
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacion(String emailDestino, String enlaceRecuperacion) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        
        // El correo remitente debe ser el tuyo configurado
        mensaje.setFrom("dahdadou@alumnat.copernic.cat");
        mensaje.setTo(emailDestino);
        mensaje.setSubject("EasyTraza - Recuperar Contraseña");
        
        // Texto del correo
        String contenido = "Hola,\n\n"
                + "Has solicitado restablecer tu contraseña en EasyTraza.\n"
                + "Haz clic en el siguiente enlace para cambiar tu contraseña (este enlace caducará en 15 minutos):\n\n"
                + enlaceRecuperacion + "\n\n"
                + "Si no has solicitado este cambio, puedes ignorar este correo de forma segura.\n\n"
                + "Saludos,\nEasyTraza.";
                
        mensaje.setText(contenido);
        
        // ¡Mandamos el email!
        mailSender.send(mensaje);
    }
}