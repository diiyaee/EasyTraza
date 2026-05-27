/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.security;

import cat.copernic.backend.entity.Usuari;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author diyae
 */
public class UsuariUserDetails implements UserDetails {

    // Guardamos la entidad original dentro de esta clase
    private final Usuari usuari;

    public UsuariUserDetails(Usuari usuari) {
        this.usuari = usuari;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Traducimos tu booleano 'esAdmin' a roles de Spring Security
        String role = usuari.isEsAdmin() ? "ROLE_ADMIN" : "ROLE_USER";
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        // Devolvemos tu campo 'contrasenya'
        return usuari.getContrasenya();
    }

    @Override
    public String getUsername() {
        // Devolvemos tu campo 'email', ya que es el que usarás para loguearte
        return usuari.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // Devuelve true indicando que la cuenta no expira
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Devuelve true indicando que la cuenta no está bloqueada
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Devuelve true indicando que las credenciales no caducan
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Devuelve true indicando que el usuario está activo
        return true;
    }

    // Método extra muy útil: 
    // Te permite recuperar tu entidad original (Usuari) si la necesitas desde un Controlador
    public String getNomComplet() {
        return usuari.getNom() + " " + usuari.getCognoms();
    }
    
    public Usuari getUsuari() {
        return this.usuari;
    }
}