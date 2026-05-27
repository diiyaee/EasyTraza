/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.apiRestController;

import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.service.UsuariService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author diyae
 */
@RestController
@RequestMapping("/api/usuaris")
public class ApiUsuariController {
    
    @Autowired
    private UsuariService usuariService;
    
    @GetMapping
    public ResponseEntity<List<Usuari>> findAll() {

        List<Usuari> llista;

        //el transporte HTTP
        ResponseEntity<List<Usuari>> response;

        //la cabecera del transporte
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-store"); //no usar caché

        try {

            llista = usuariService.getAllUsuaris();

            response = new ResponseEntity<>(llista, headers, HttpStatus.OK);

        } catch (Exception e) {

            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
