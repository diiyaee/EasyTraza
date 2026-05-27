/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.entity.AlbaraClient;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author diyae
 */
public interface AlbaraClientRepository extends JpaRepository<AlbaraClient, Long> {
    
    Optional<AlbaraClient> findByClient_IdAndDataProduccio(Long clientId, LocalDate dataProduccio);
}
