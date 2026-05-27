/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.entity.Usuari;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diyae
 */
@Repository
public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    
    List<Usuari> findByNom(String nom);

    List<Usuari> findByCognoms(String cognoms);
    
    Optional<Usuari> findByEmail(String email);

    List<Usuari> findByEsAdmin(boolean esAdmin);
    
    boolean existsByNomIgnoreCaseAndCognomsIgnoreCase(String nom, String cognoms);

    boolean existsByNomIgnoreCaseAndCognomsIgnoreCaseAndIdNot(String nom, String cognoms, Long id);
    
    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}