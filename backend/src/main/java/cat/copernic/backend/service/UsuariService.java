/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.DTOs.PerfilDTO;
import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.repository.UsuariRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author diyae
 */
@Service
public class UsuariService {
    
    @Autowired
    private UsuariRepository usuariRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Usuari> getAllUsuaris() {
        return usuariRepository.findAll();
    }
    
    public Usuari getUsuariById(Long id) {
        return usuariRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    
    private String normalitzar(String text) {
        if (text == null) return null;

        return text
                .trim()
                .replaceAll("\\s+", " ");
    }
    
    public Usuari crearUsuari(Usuari u) {

        String nom = normalitzar(u.getNom());
        String cognoms = normalitzar(u.getCognoms());
        String email = u.getEmail() != null ? u.getEmail().trim().toLowerCase() : null;

        if (usuariRepository.existsByNomIgnoreCaseAndCognomsIgnoreCase(nom, cognoms)) {
            throw new IllegalArgumentException("Ya existe un usuario con este nombre y apellidos.");
        }

        if (email != null && usuariRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con este email.");
        }

        u.setNom(nom);
        u.setCognoms(cognoms);
        u.setEmail(email);

        if (u.getContrasenya() == null || u.getContrasenya().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        u.setContrasenya(passwordEncoder.encode(u.getContrasenya()));

        return usuariRepository.save(u);
    }
    
    public Usuari editarUsuari(Long id, Usuari dades) {

        String email = dades.getEmail() != null ? dades.getEmail().trim().toLowerCase() : null;

        if (usuariRepository.existsByNomIgnoreCaseAndCognomsIgnoreCaseAndIdNot(
                dades.getNom(), dades.getCognoms(), id)) {

            throw new IllegalArgumentException("Ya existe un usuario con este nombre y apellidos.");
        }

        if (email != null && usuariRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new IllegalArgumentException("Ya existe un usuario con este email.");
        }

        Usuari u = getUsuariById(id);

        u.setNom(normalitzar(dades.getNom()));
        u.setCognoms(normalitzar(dades.getCognoms()));
        u.setEmail(email);

        if (dades.getContrasenya() != null && !dades.getContrasenya().isEmpty()) {
            u.setContrasenya(passwordEncoder.encode(dades.getContrasenya()));
        }
        
        if (dades.getFotoPerfil() != null) {
            u.setFotoPerfil(dades.getFotoPerfil());
        }

        u.setEsAdmin(dades.isEsAdmin());

        return usuariRepository.save(u);
    }
    
    @Transactional
    public void actualizarMiPerfil(Long idUsuarioLogueado, PerfilDTO datosNuevos) {

        Usuari usuariActual = usuariRepository.findById(idUsuarioLogueado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuariActual.getEmail().equals(datosNuevos.getEmail())) {
            if (usuariRepository.findByEmail(datosNuevos.getEmail()).isPresent()) {
                throw new RuntimeException("Este correo ya está registrado por otro usuario.");
            }
            usuariActual.setEmail(datosNuevos.getEmail());
        }

        usuariActual.setNom(datosNuevos.getNom());
        usuariActual.setCognoms(datosNuevos.getCognoms());

        if (datosNuevos.getContrasenyaNueva() != null && !datosNuevos.getContrasenyaNueva().trim().isEmpty()) {
            if (datosNuevos.getContrasenyaNueva().length() < 6) {
                throw new RuntimeException("La nueva contraseña debe tener al menos 6 carácteres.");
            }
            usuariActual.setContrasenya(passwordEncoder.encode(datosNuevos.getContrasenyaNueva()));
        }

        if (datosNuevos.getFotoPerfil() != null) {
            usuariActual.setFotoPerfil(datosNuevos.getFotoPerfil());
        }

        usuariRepository.save(usuariActual);
    }
    
    public void eliminarUsuari(Long id) {
        try {
            usuariRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "No se ha podido eliminar el usuario porque tiene albaranes asociados."
            );
        }
    }
    
    public List<Usuari> filtrar(String query) {
        List<Usuari> usuaris = usuariRepository.findAll();

        if (query != null && !query.isBlank()) {
            String lowerQuery = query.toLowerCase();
            return usuaris.stream()
                    .filter(u -> {
                        String nomComplet = (u.getNom() != null ? u.getNom() : "") + " " + 
                                           (u.getCognoms() != null ? u.getCognoms() : "");
                        return nomComplet.toLowerCase().contains(lowerQuery);
                    })
                    .toList();
        }
        return usuaris;
    }

    public List<Usuari> ordenarUsuaris(List<Usuari> usuaris, boolean asc) {
        // Creamos un comparador que concatena nombre y apellidos
        Comparator<Usuari> comparator = Comparator.comparing(
                u -> ( (u.getNom() != null ? u.getNom() : "") + " " + 
                       (u.getCognoms() != null ? u.getCognoms() : "") ).toLowerCase()
        );

        if (!asc) {
            comparator = comparator.reversed();
        }

        return usuaris.stream().sorted(comparator).toList();
    }
}
