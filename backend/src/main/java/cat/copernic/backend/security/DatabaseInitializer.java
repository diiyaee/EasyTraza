/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.security;

import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author diyae
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UsuariRepository usuariRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // Correo del usuario administrador de prueba
        String emailAdmin = "admin@easytraza.com";

        // Comprobamos si el usuario ya existe para no crearlo duplicado cada vez que reinicias el backend
        if (usuariRepository.findByEmail(emailAdmin).isEmpty()) {
            
            Usuari admin = new Usuari();
            admin.setNom("Jefe");
            admin.setCognoms("Admin EasyTraza");
            admin.setEmail(emailAdmin);
            
            // ¡MAGIA! Encriptamos la contraseña "admin123" antes de guardarla
            admin.setContrasenya(passwordEncoder.encode("admin123"));
            
            // Le damos el rol de administrador
            admin.setEsAdmin(true);

            // Lo guardamos en la base de datos
            usuariRepository.save(admin);
            
            System.out.println("=====================================================");
            System.out.println("✅ USUARIO ADMIN DE PRUEBA CREADO CON ÉXITO");
            System.out.println("📧 Correo: " + emailAdmin);
            System.out.println("🔑 Contraseña: admin123");
            System.out.println("=====================================================");
        }
    }
}