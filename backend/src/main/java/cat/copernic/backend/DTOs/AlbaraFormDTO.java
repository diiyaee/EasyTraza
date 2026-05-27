/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

import cat.copernic.backend.entity.AlbaraProveidor;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author diyae
 */
public class AlbaraFormDTO {
    private String numAlbara;
    private Long proveidorId;
    private LocalDate dataRecepcio;
    private Long usuariId;
    private List<LiniaDTO> linies;

    public String getNumAlbara() {
        return numAlbara;
    }

    public void setNumAlbara(String numAlbara) {
        this.numAlbara = numAlbara;
    }

    public Long getProveidorId() {
        return proveidorId;
    }

    public void setProveidorId(Long proveidorId) {
        this.proveidorId = proveidorId;
    }

    public LocalDate getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(LocalDate dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public Long getUsuariId() {
        return usuariId;
    }

    public void setUsuariId(Long usuariId) {
        this.usuariId = usuariId;
    }

    public List<LiniaDTO> getLinies() {
        return linies;
    }

    public void setLinies(List<LiniaDTO> linies) {
        this.linies = linies;
    }
    
    
}