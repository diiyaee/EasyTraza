/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author diyae
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. Lo público
                .requestMatchers("/css/**", "/js/**", "/img/**", "/login", "/recuperar-password/**").permitAll()
                .requestMatchers("/api/**").permitAll() 

                // 2. ⛔ RUTAS RESTRINGIDAS (Solo Administradores)
                // Añade aquí todas las rutas que un trabajador normal NO debería ver
                .requestMatchers("/usuaris/**", "/proveidors/**", "/clients/**", "/productes/**", "/materies/**").hasRole("ADMIN")

                // 3. El resto de la web: accesible para cualquier usuario (Admin o Normal)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // Le decimos a Spring cuál es nuestra ruta de login personalizada
                .loginPage("/login") 
                
                // Aunque introduzcan su email, Spring Security espera que el campo HTML se llame "username"
                .usernameParameter("username") 
                .passwordParameter("password")
                
                .defaultSuccessUrl("/albarans-proveidor", true) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // Ruta que intercepta Spring para cerrar sesión
                .logoutSuccessUrl("/login?logout") // A dónde nos envía tras salir (para mostrar un mensaje verde)
                .invalidateHttpSession(true) // Destruye la sesión
                .clearAuthentication(true) // Borra los datos del usuario logueado
                .permitAll()
            )
            // Desactivamos la protección CSRF SOLO para la API móvil, si no, tus peticiones POST desde Android fallarán
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Le indicamos a Spring Security que nuestras contraseñas se encriptarán usando BCrypt
        return new BCryptPasswordEncoder(); 
    }
}