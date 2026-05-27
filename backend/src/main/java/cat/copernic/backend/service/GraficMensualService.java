/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.DTOs.GraficMensualDTO;
import cat.copernic.backend.DTOs.VentaDiariaDTO;
import cat.copernic.backend.repository.LiniaClientRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author diyae
 */
@Service
public class GraficMensualService {

    @Autowired
    private LiniaClientRepository liniaClientRepository;

    public GraficMensualDTO obtenirEstadistiquesMensuals(int any, int mes, Long producteId) {

        List<VentaDiariaDTO> datos =
                liniaClientRepository.findVentasDiarias(any, mes, producteId);

        Map<Integer, Double> mapa = new HashMap<>();

        for (VentaDiariaDTO v : datos) {
            mapa.put(v.getDia(), v.getCantidad());
        }

        YearMonth yearMonth = YearMonth.of(any, mes);
        int diasDelMes = yearMonth.lengthOfMonth();

        List<VentaDiariaDTO> datosCompletos = new ArrayList<>();

        for (int dia = 1; dia <= diasDelMes; dia++) {

            Double cantidad = mapa.getOrDefault(dia, 0.0);

            datosCompletos.add(
                    new VentaDiariaDTO(dia, cantidad)
            );
        }

        Double totalMes = datosCompletos.stream()
            .map(VentaDiariaDTO::getCantidad)
            .reduce(0.0, Double::sum);

        return new GraficMensualDTO(
                datosCompletos,
                totalMes,
                mes,
                any
        );
    }
}