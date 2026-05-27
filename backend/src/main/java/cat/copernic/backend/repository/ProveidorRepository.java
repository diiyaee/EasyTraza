/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.entity.Proveidor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diyae
 */
@Repository
public interface ProveidorRepository extends JpaRepository<Proveidor, Long> {
    
    Optional<Proveidor> findByNif(String nif);
    
    Optional<Proveidor> findByNom(String nom);
    
    boolean existsByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);
    
    boolean existsByNif(String nif);
    
    boolean existsByTelefon(String telefon);
    
    boolean existsByTelefonAndIdNot(String telefon, Long id);
    
    boolean existsByAdreca(String adreca);
    
    boolean existsByAdrecaAndIdNot(String adreca, Long id);
}
