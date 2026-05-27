/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

/**
 *
 * @author diyae
 */
public class VentaDiariaDTO {

    private Integer dia;
    private Double cantidad;

    public VentaDiariaDTO(Integer dia, Double cantidad) {
        this.dia = dia;
        this.cantidad = cantidad;
    }

    public Integer getDia() {
        return dia;
    }

    public Double getCantidad() {
        return cantidad;
    }
}