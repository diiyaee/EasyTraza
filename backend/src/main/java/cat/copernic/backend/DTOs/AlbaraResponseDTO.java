/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

import cat.copernic.backend.entity.AlbaraProveidor;
import cat.copernic.backend.entity.Usuari;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author diyae
 */
public class AlbaraResponseDTO {
    private String numAlbara;
    private LocalDate dataRecepcio;
    private ProveidorDTO proveidor;
    private Usuari usuari; // <-- 1. NUEVO CAMPO
    private List<LiniaDTO> linies;

    public AlbaraResponseDTO(AlbaraProveidor a) {
        this.numAlbara = a.getNumAlbara();
        this.dataRecepcio = a.getDataRecepcio();
        
        this.usuari = a.getRegistratPer(); 

        this.proveidor = new ProveidorDTO(
            a.getProveidor().getId(),
            a.getProveidor().getNom()
        );

        this.linies = a.getLinies() == null
            ? new ArrayList<>()
            : new ArrayList<>(a.getLinies())
                .stream()
                .map(LiniaDTO::new)
                .toList();
    }

    public String getNumAlbara() {
        return numAlbara;
    }

    public void setNumAlbara(String numAlbara) {
        this.numAlbara = numAlbara;
    }

    public LocalDate getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(LocalDate dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public ProveidorDTO getProveidor() {
        return proveidor;
    }

    public void setProveidor(ProveidorDTO proveidor) {
        this.proveidor = proveidor;
    }

    public Usuari getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuari usuari) {
        this.usuari = usuari;
    }

    public List<LiniaDTO> getLinies() {
        return linies;
    }

    public void setLinies(List<LiniaDTO> linies) {
        this.linies = linies;
    }
}