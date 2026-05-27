/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

import cat.copernic.backend.entity.Lot;
import cat.copernic.backend.enums.EstatLot;

/**
 *
 * @author diyae
 */
public class LotDTO {

    private Long id;

    private String numLot;

    private EstatLot estatLot;

    private String materiaNom;

    private String proveidorNom;

    public LotDTO() {
    }

    public LotDTO(Lot lot) {

        this.id = lot.getId();
        this.numLot = lot.getNumLot();
        this.estatLot = lot.getEstatLot();

        this.materiaNom = lot.getMateria().getNom();

        this.proveidorNom = lot.getProveidor().getNom();
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

    public EstatLot getEstatLot() {
        return estatLot;
    }

    public void setEstatLot(EstatLot estatLot) {
        this.estatLot = estatLot;
    }

    public String getMateriaNom() {
        return materiaNom;
    }

    public void setMateriaNom(String materiaNom) {
        this.materiaNom = materiaNom;
    }

    public String getProveidorNom() {
        return proveidorNom;
    }

    public void setProveidorNom(String proveidorNom) {
        this.proveidorNom = proveidorNom;
    }
}