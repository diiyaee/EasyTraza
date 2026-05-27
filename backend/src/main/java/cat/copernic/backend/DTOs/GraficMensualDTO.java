/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.DTOs;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author diyae
 */
public class GraficMensualDTO {

    private List<VentaDiariaDTO> datos;
    private Double totalMes;
    private int mes;
    private int any;

    public GraficMensualDTO() {
    }

    public GraficMensualDTO(List<VentaDiariaDTO> datos, Double totalMes, int mes, int any) {
        this.datos = datos;
        this.totalMes = totalMes;
        this.mes = mes;
        this.any = any;
    }

    public List<VentaDiariaDTO> getDatos() {
        return datos;
    }

    public Double getTotalMes() {
        return totalMes;
    }

    public int getMes() {
        return mes;
    }

    public int getAny() {
        return any;
    }
}