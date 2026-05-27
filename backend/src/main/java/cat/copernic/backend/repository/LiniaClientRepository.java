/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package cat.copernic.backend.repository;

import cat.copernic.backend.DTOs.VentaDiariaDTO;
import cat.copernic.backend.entity.LiniaClient;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author diyae
 */
public interface LiniaClientRepository extends JpaRepository<LiniaClient, Long> {
    
    @Query("""
    SELECT new cat.copernic.backend.DTOs.VentaDiariaDTO(
        DAY(a.dataProduccio),
        SUM(l.quantitat)
    )
    FROM LiniaClient l
    JOIN l.albara a
    WHERE a.estatAlbaraClient = cat.copernic.backend.enums.EstatAlbaraClient.LLIURAT
      AND YEAR(a.dataProduccio) = :any
      AND MONTH(a.dataProduccio) = :mes
      AND (:producteId IS NULL OR l.producte.id = :producteId)
    GROUP BY DAY(a.dataProduccio)
    ORDER BY DAY(a.dataProduccio)
    """)
    List<VentaDiariaDTO> findVentasDiarias(
            @Param("any") int any,
            @Param("mes") int mes,
            @Param("producteId") Long producteId
    );
    
    @Query("""
        SELECT l FROM LiniaClient l
        WHERE l.albara.dataProduccio BETWEEN :inici AND :fi
    """)
    List<LiniaClient> findProduccioEntreDates(LocalDate inici, LocalDate fi);
}
