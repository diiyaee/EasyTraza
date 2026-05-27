/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.repository.UsuariRepository;
import cat.copernic.backend.security.UsuariUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author diyae
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuariRepository usuariRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Buscamos a tu usuario por email (que es lo que escribe en el login)
        Usuari usuari = usuariRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con correo " + email + " no encontrado"));

        // 2. Lo envolvemos en la clase que creamos antes y lo devolvemos.
        // ¡Esa clase ya se encarga internamente de decirle a Spring el email, el rol y la contraseña!
        return new UsuariUserDetails(usuari);
    }
}