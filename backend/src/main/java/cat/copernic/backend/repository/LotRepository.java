/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.entity.Lot;
import cat.copernic.backend.enums.EstatLot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diyae
 */
@Repository
public interface LotRepository extends JpaRepository<Lot, Long> {

    Optional<Lot> findByNumLotAndProveidor_Id(String numLot, Long proveidorId);

    Optional<Lot> findByNumLot(String numLot);
    
    Optional<Lot> findByMateria_IdAndEstatLot(Long materiaId, EstatLot estatLot);
}