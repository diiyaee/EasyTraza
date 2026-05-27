/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.entity;

import cat.copernic.backend.enums.EstatLot;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author diyae
 */
@Entity
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de lote es obligatorio")
    @Column(nullable = false)
    private String numLot;
    
    @ManyToOne
    @NotNull(message = "La materia es obligatoria")
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne
    @NotNull(message = "El proveedor es obligatorio")
    @JoinColumn(name = "proveidor_id", nullable = false)
    private Proveidor proveidor;

    @NotNull(message = "La fecha de caducidad es obligatoria")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataCaducitat;
    
    @Enumerated(EnumType.STRING)
    private EstatLot estatLot = EstatLot.EN_ESTOC;

    private LocalDateTime dataObertura;

    private LocalDateTime dataAcabament;

    public Lot() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumLot() {
        return numLot;
    }

    public void setNumLot(String numLot) {
        this.numLot = numLot;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }
    
    public Proveidor getProveidor() {
        return proveidor;
    }

    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }

    public LocalDate getDataCaducitat() {
        return dataCaducitat;
    }

    public void setDataCaducitat(LocalDate dataCaducitat) {
        this.dataCaducitat = dataCaducitat;
    }

    public EstatLot getEstatLot() {
        return estatLot;
    }

    public void setEstatLot(EstatLot estatLot) {
        this.estatLot = estatLot;
    }

    public LocalDateTime getDataObertura() {
        return dataObertura;
    }

    public void setDataObertura(LocalDateTime dataObertura) {
        this.dataObertura = dataObertura;
    }

    public LocalDateTime getDataAcabament() {
        return dataAcabament;
    }

    public void setDataAcabament(LocalDateTime dataAcabament) {
        this.dataAcabament = dataAcabament;
    }
}