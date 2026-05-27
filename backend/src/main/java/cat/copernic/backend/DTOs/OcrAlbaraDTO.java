/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

import java.util.List;

/**
 *
 * @author diyae
 */
public class OcrAlbaraDTO {

    private String numAlbara;
    private String proveidor;
    private String dataRecepcio;

    private List<OcrLiniaDTO> linies;

    public OcrAlbaraDTO() {
    }

    public String getNumAlbara() {
        return numAlbara;
    }

    public void setNumAlbara(String numAlbara) {
        this.numAlbara = numAlbara;
    }

    public String getProveidor() {
        return proveidor;
    }

    public void setProveidor(String proveidor) {
        this.proveidor = proveidor;
    }

    public String getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(String dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public List<OcrLiniaDTO> getLinies() {
        return linies;
    }

    public void setLinies(List<OcrLiniaDTO> linies) {
        this.linies = linies;
    }
}