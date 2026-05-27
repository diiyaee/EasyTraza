/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

/**
 *
 * @author diyae
 */
@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El código secreto único
    @Column(nullable = false, unique = true)
    private String token;

    // Relación con el usuario que ha pedido cambiar la contraseña
    @OneToOne(targetEntity = Usuari.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "usuari_id")
    private Usuari queryUsuario;

    // Fecha en la que el enlace dejará de funcionar
    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, Usuari usuari, int minutosValidez) {
        this.token = token;
        this.queryUsuario = usuari;
        // Calculamos la fecha de caducidad sumando los minutos deseados a la hora actual
        this.fechaExpiracion = LocalDateTime.now().plusMinutes(minutosValidez);
    }

    // Método rápido para saber si el token ha caducado
    public boolean estaCaducado() {
        return LocalDateTime.now().isAfter(this.fechaExpiracion);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Usuari getQueryUsuario() { return queryUsuario; }
    public void setQueryUsuario(Usuari usuari) { this.queryUsuario = usuari; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}