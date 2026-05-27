/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

import cat.copernic.backend.entity.LiniaProveidor;
import java.time.LocalDate;

/**
 *
 * @author diyae
 */
public class LiniaDTO {
    private String numLot;
    private Long materiaId;
    private Double quantitat;
    private String unitats;
    private LocalDate dataCaducitat;

    public LiniaDTO(LiniaProveidor l) {

        this.numLot = l.getLot() != null ? l.getLot().getNumLot() : null;

        this.materiaId = (l.getLot() != null && l.getLot().getMateria() != null)
                ? l.getLot().getMateria().getId()
                : null;

        this.quantitat = l.getQuantitat();
        this.unitats = l.getUnitats();

        this.dataCaducitat = l.getLot() != null
                ? l.getLot().getDataCaducitat()
                : null;
    }

    public LiniaDTO() {
    }
    
    public String getNumLot() {
        return numLot;
    }

    public void setNumLot(String numLot) {
        this.numLot = numLot;
    }

    public Long getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(Long materiaId) {
        this.materiaId = materiaId;
    }

    public Double getQuantitat() {
        return quantitat;
    }

    public void setQuantitat(Double quantitat) {
        this.quantitat = quantitat;
    }

    public String getUnitats() {
        return unitats;
    }

    public void setUnitats(String unitats) {
        this.unitats = unitats;
    }

    public LocalDate getDataCaducitat() {
        return dataCaducitat;
    }

    public void setDataCaducitat(LocalDate dataCaducitat) {
        this.dataCaducitat = dataCaducitat;
    }
    
    
}