/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.apiRestController;

import cat.copernic.backend.entity.Producte;
import cat.copernic.backend.repository.ProducteRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author diyae
 */
@RestController
@RequestMapping("/api/productes")
public class ApiProducteController {

    @Autowired
    private ProducteRepository producteRepository;

    @GetMapping
    public List<Producte> getProductes() {
        return producteRepository.findAll();
    }
}