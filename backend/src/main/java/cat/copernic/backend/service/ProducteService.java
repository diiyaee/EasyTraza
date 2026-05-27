/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.Producte;
import cat.copernic.backend.repository.ProducteRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author diyae
 */
@Service
public class ProducteService {
    
    @Autowired
    private ProducteRepository producteRepository;
    
    public List<Producte> getAllProductes() {
        return producteRepository.findAll();
    }
    
    public Producte getProducteById(Long id) {
        return producteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }
    
    private String normalitzar(String text) {
        if (text == null) return null;

        return text
                .trim()
                .replaceAll("\\s+", " ");
    }
    
    public Producte crearProducte(Producte p) {

        String nom = normalitzar(p.getNom());

        if (producteRepository.existsByNomIgnoreCase(nom)) {
            throw new IllegalArgumentException("Ya existe un producto con este nombre.");
        }

        p.setNom(nom);

        return producteRepository.save(p);
    }
    
    public Producte editarProducte(Long id, Producte dades) {

        String nom = normalitzar(dades.getNom());

        if (producteRepository.existsByNomIgnoreCaseAndIdNot(nom, id)) {
            throw new IllegalArgumentException("Ya existe un producto con este nombre.");
        }

        Producte p = getProducteById(id);

        p.setNom(nom);

        if (dades.getDescripcio() != null) {
            p.setDescripcio(dades.getDescripcio());
        }

        return producteRepository.save(p);
    }
    
    public void eliminarProducte(Long id) {
        try {
            producteRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "No se ha podido eliminar el producto porque hay líneas de producción asociadas."
            );
        }
    }
    
    public List<Producte> filtrar(String nom) {
        List<Producte> productes = producteRepository.findAll();

        if (nom != null && !nom.isBlank()) {
            productes = productes.stream()
                    .filter(p -> p.getNom() != null &&
                            p.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .toList();
        }
        return productes;
    }

    public List<Producte> ordenarProductes(List<Producte> productes, String sortField, boolean asc) {
        // Por defecto ordenamos por nombre
        if (sortField == null || !sortField.equals("nom")) sortField = "nom";

        Comparator<Producte> comparator = Comparator.comparing(
                Producte::getNom, 
                Comparator.nullsLast(String::compareToIgnoreCase)
        );

        if (!asc) {
            comparator = comparator.reversed();
        }

        return productes.stream().sorted(comparator).toList();
    }
}