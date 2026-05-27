/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.Proveidor;
import cat.copernic.backend.repository.ProveidorRepository;
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
public class ProveidorService {
    
    @Autowired
    private ProveidorRepository proveidorRepository;
    
    public List<Proveidor> getAllProveidors() {
        return proveidorRepository.findAll();
    }
    
    public Proveidor getProveidorById(Long id) {
        return proveidorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));
    }
    
    private String normalitzar(String text) {
        if (text == null) return null;

        return text
                .trim()
                .replaceAll("\\s+", " ");
    }
    
    public Proveidor crearProveidor(Proveidor p) {

        String nom = normalitzar(p.getNom());
        String adreca = normalitzar(p.getAdreca());

        if (proveidorRepository.existsByNomIgnoreCase(nom)) {
            throw new IllegalArgumentException("Ya existe un proveedor con este nombre.");
        }
        
        if (proveidorRepository.existsByNif(p.getNif())) {
            throw new IllegalArgumentException("Ya existe un proveedor con este NIF.");
        }
        
        if (proveidorRepository.existsByTelefon(p.getTelefon())) {
            throw new IllegalArgumentException("Ya existe un proveedor con este teléfono.");
        }
        
        if (proveidorRepository.existsByAdreca(adreca)) {
            throw new IllegalArgumentException("Ya existe un proveedor con esta dirección.");
        }

        p.setNom(nom);
        p.setAdreca(adreca);
        
        return proveidorRepository.save(p);
    }
    
    public Proveidor editarProveidor(Long id, Proveidor dades) {

        
        if (proveidorRepository.existsByNomIgnoreCaseAndIdNot(dades.getNom(), id)) {
            throw new IllegalArgumentException("Ya existe un proveedor con este nombre.");
        }
        
        if (proveidorRepository.existsByTelefonAndIdNot(dades.getTelefon(), id)) {
            throw new IllegalArgumentException("Ya existe un proveedor con este teléfono.");
        }
        
        if (proveidorRepository.existsByAdrecaAndIdNot(dades.getAdreca(), id)) {
            throw new IllegalArgumentException("Ya existe un proveedor con esta dirección.");
        }
        
        Proveidor p = getProveidorById(id);

        p.setNif(dades.getNif());
        p.setNom(normalitzar(dades.getNom()));
        p.setTelefon(dades.getTelefon());
        p.setAdreca(normalitzar(dades.getAdreca()));
        p.setObservacions(dades.getObservacions());

        return proveidorRepository.save(p);
    }
    
    public void eliminarProveidor(Long id) {
        try {
            proveidorRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "No se ha podido eliminar el proveedor porque tiene albaranes asociados."
            );
        }
    }
    
    public List<Proveidor> filtrar(String nom, String nif, String telefon, String adreca) {
        List<Proveidor> proveidors = proveidorRepository.findAll();

        if (nom != null && !nom.isBlank()) {
            proveidors = proveidors.stream()
                    .filter(p -> p.getNom() != null && p.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .toList();
        }
        if (nif != null && !nif.isBlank()) {
            proveidors = proveidors.stream()
                    .filter(p -> p.getNif() != null && p.getNif().toLowerCase().contains(nif.toLowerCase()))
                    .toList();
        }
        if (telefon != null && !telefon.isBlank()) {
            proveidors = proveidors.stream()
                    .filter(p -> p.getTelefon() != null && p.getTelefon().toLowerCase().contains(telefon.toLowerCase()))
                    .toList();
        }
        if (adreca != null && !adreca.isBlank()) {
            proveidors = proveidors.stream()
                    .filter(p -> p.getAdreca() != null && p.getAdreca().toLowerCase().contains(adreca.toLowerCase()))
                    .toList();
        }
        return proveidors;
    }

    public List<Proveidor> ordenarProveidors(List<Proveidor> proveidors, String sortField, boolean asc) {
        if (sortField == null) sortField = "nom";

        Comparator<Proveidor> comparator;

        switch (sortField) {
            case "nif":
                comparator = Comparator.comparing(Proveidor::getNif, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "telefon":
                comparator = Comparator.comparing(Proveidor::getTelefon, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "adreca":
                comparator = Comparator.comparing(Proveidor::getAdreca, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "nom":
            default:
                comparator = Comparator.comparing(Proveidor::getNom, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
        }

        if (!asc) {
            comparator = comparator.reversed();
        }

        return proveidors.stream().sorted(comparator).toList();
    }
}
