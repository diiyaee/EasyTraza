/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.entity.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diyae
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByNif(String nif);
    
    List<Client> findByNom(String nom);
    
    List<Client> findByCognoms(String cognoms);
    
    boolean existsByNomIgnoreCaseAndCognomsIgnoreCase(String nom, String cognoms);

    boolean existsByNomIgnoreCaseAndCognomsIgnoreCaseAndIdNot(String nom, String cognoms, Long id);
    
    boolean existsByNif(String nif);
    
    boolean existsByTelefon(String telefon);
    
    boolean existsByTelefonAndIdNot(String telefon, Long id);
    
    boolean existsByAdreca(String adreca);
    
    boolean existsByAdrecaAndIdNot(String adreca, Long id);
}