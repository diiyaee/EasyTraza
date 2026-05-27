/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.entity.Materia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diyae
 */
@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    
    Optional<Materia> findByNom(String nom);
    
    boolean existsByNomIgnoreCase(String nom);
    
    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);
}
