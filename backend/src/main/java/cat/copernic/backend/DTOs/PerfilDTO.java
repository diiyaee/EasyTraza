/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 *
 * @author diyae
 */
public class PerfilDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "El nombre solo puede contener letras")
    private String nom;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100)
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "Los apellidos solo pueden contener letras")
    private String cognoms;

    @Email
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    private String contrasenyaNueva;
    
    private String fotoPerfil;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getCognoms() { return cognoms; }
    public void setCognoms(String cognoms) { this.cognoms = cognoms; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getContrasenyaNueva() { return contrasenyaNueva; }
    public void setContrasenyaNueva(String contrasenyaNueva) { this.contrasenyaNueva = contrasenyaNueva; }
    public String getFotoPerfil() {return fotoPerfil;}
    public void setFotoPerfil(String fotoPerfil) {this.fotoPerfil = fotoPerfil;}
    
}