/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.apiRestController;

import cat.copernic.backend.entity.Proveidor;
import cat.copernic.backend.repository.ProveidorRepository;
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
@RequestMapping("/api/proveidors")
public class ApiProveidorController {

    @Autowired
    private ProveidorRepository proveidorRepository;

    @GetMapping
    public List<Proveidor> getAll() {
        return proveidorRepository.findAll();
    }
}