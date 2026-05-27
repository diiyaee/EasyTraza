/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.entity.AlbaraProveidor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author diyae
 */
@Repository
public interface AlbaraProveidorRepository extends JpaRepository<AlbaraProveidor, Long> {

    boolean existsByNumAlbara(String numAlbara);

    public Optional<AlbaraProveidor> findByNumAlbara(String numAlbara);

}