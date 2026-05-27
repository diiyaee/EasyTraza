/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author diyae
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String mostrarLogin() {
        // Esto le dice a Spring que busque el archivo "login.html" dentro de la carpeta "templates"
        return "login";
    }
}