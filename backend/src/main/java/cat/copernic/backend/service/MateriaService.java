/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.Materia;
import cat.copernic.backend.repository.MateriaRepository;
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
public class MateriaService {
    
    @Autowired
    private MateriaRepository materiaRepository;
    
    public List<Materia> getAllMateries() {
        return materiaRepository.findAll();
    }
    
    public Materia getMateriaById(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada"));
    }
    
    private String normalitzar(String text) {
        if (text == null) return null;

        return text
                .trim()
                .replaceAll("\\s+", " ");
    }
    
    public Materia crearMateria(Materia m) {

        String nom = normalitzar(m.getNom());

        if (materiaRepository.existsByNomIgnoreCase(nom)) {
            throw new IllegalArgumentException("Ya existe una materia con este nombre.");
        }

        m.setNom(nom);

        return materiaRepository.save(m);
    }
    
    public Materia editarMateria(Long id, Materia dades) {

        String nom = normalitzar(dades.getNom());
        
        if (materiaRepository.existsByNomIgnoreCaseAndIdNot(
                dades.getNom(), id)) {

            throw new IllegalArgumentException("Ya existe una materia con este nombre.");
        }
        
        Materia m = getMateriaById(id);

        m.setNom(nom);

        return materiaRepository.save(m);
    }
    
    public void eliminarMateria(Long id) {
        try {
            materiaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "No se ha podido eliminar la materia porque hay lotes asociados."
            );
        }
    }
    
    public List<Materia> filtrar(String nom) {
        List<Materia> materies = materiaRepository.findAll();

        if (nom != null && !nom.isBlank()) {
            return materies.stream()
                    .filter(m -> m.getNom() != null && 
                                 m.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .toList();
        }
        return materies;
    }

    public List<Materia> ordenarMateries(List<Materia> materies, String sortField, boolean asc) {
        // Por defecto ordenamos por nombre
        if (sortField == null || !sortField.equals("nom")) sortField = "nom";

        Comparator<Materia> comparator = Comparator.comparing(
                Materia::getNom, 
                Comparator.nullsLast(String::compareToIgnoreCase)
        );

        if (!asc) {
            comparator = comparator.reversed();
        }

        return materies.stream().sorted(comparator).toList();
    }
}
