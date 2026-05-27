/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.DTOs.GraficMensualDTO;
import cat.copernic.backend.service.GraficMensualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/grafic-mensual")
public class GraficMensualController {

    @Autowired
    private GraficMensualService graficMensualService;

    @GetMapping
    public String vistaGraficMensual() {
        return "graficMensual";
    }

    @ResponseBody
    @GetMapping("/data")
    public GraficMensualDTO obtenirDadesMensuals(
            @RequestParam int any,
            @RequestParam int mes,
            @RequestParam(required = false) Long producteId
    ) {
        return graficMensualService.obtenirEstadistiquesMensuals(any, mes, producteId);
    }
}