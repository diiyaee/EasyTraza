/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.LiniaClient;
import cat.copernic.backend.entity.Lot;
import cat.copernic.backend.repository.LiniaClientRepository;
import cat.copernic.backend.repository.LotRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author diyae
 */
@Service
public class ProduccioLotService {

    @Autowired
    private LotRepository lotRepository;

    @Autowired
    private LiniaClientRepository liniaClientRepository;

    public List<LiniaClient> obtenirProduccioLot(Long lotId) {

        Lot lot = lotRepository.findById(lotId)
                .orElseThrow(() -> new RuntimeException("Lot no trobat"));

        // Si el lote no se ha abierto, es imposible que haya producido nada
        if (lot.getDataObertura() == null) {
            return List.of(); 
        }

        LocalDate inici = lot.getDataObertura().toLocalDate();
        LocalDate fi = (lot.getDataAcabament() != null)
                ? lot.getDataAcabament().toLocalDate()
                : LocalDate.now();

        // 1. Obtenemos todas las líneas producidas en ese rango de fechas
        List<LiniaClient> totesLesLinies = liniaClientRepository.findProduccioEntreDates(inici, fi);

        // 2. Filtramos estrictamente para quedarnos SOLO con las asociadas a un albarán 'LLIURAT'
        return totesLesLinies.stream()
                .filter(l -> l.getAlbara() != null 
                          && l.getAlbara().getEstatAlbaraClient() == cat.copernic.backend.enums.EstatAlbaraClient.LLIURAT)
                .toList();
    }
    
    public List<LiniaClient> filtrar(List<LiniaClient> linies, Long producteId, Long clientId, LocalDate dataProduccio) {
        return linies.stream()
                // Filtro por Producto (Búsqueda exacta por ID)
                .filter(l -> producteId == null || 
                        (l.getProducte() != null && l.getProducte().getId().equals(producteId)))
                // Filtro por Cliente (Búsqueda exacta por ID)
                .filter(l -> clientId == null || 
                        (l.getAlbara() != null && l.getAlbara().getClient() != null && l.getAlbara().getClient().getId().equals(clientId)))
                // Filtro por Fecha de Producción
                .filter(l -> dataProduccio == null || 
                        (l.getAlbara() != null && l.getAlbara().getDataProduccio() != null && l.getAlbara().getDataProduccio().toString().startsWith(dataProduccio.toString())))
                .toList();
    }

    public List<LiniaClient> ordenar(List<LiniaClient> linies, String sortField, boolean asc) {
        if (sortField == null) sortField = "dataProduccio";

        Comparator<LiniaClient> comparator;

        switch (sortField) {
            case "producte":
                comparator = Comparator.comparing(
                        l -> l.getProducte() != null ? l.getProducte().getNom() : "",
                        String.CASE_INSENSITIVE_ORDER
                );
                break;
            case "client":
                comparator = Comparator.comparing(
                        l -> (l.getAlbara() != null && l.getAlbara().getClient() != null) ? l.getAlbara().getClient().getNom() : "",
                        String.CASE_INSENSITIVE_ORDER
                );
                break;
            case "quantitat":
                comparator = Comparator.comparing(LiniaClient::getQuantitat, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "dataProduccio":
            default:
                // Al estar las fechas en formato ISO (YYYY-MM-DD), compararlas como texto funciona perfectamente
                comparator = (l1, l2) -> {
                    String d1 = (l1.getAlbara() != null && l1.getAlbara().getDataProduccio() != null) ? l1.getAlbara().getDataProduccio().toString() : "";
                    String d2 = (l2.getAlbara() != null && l2.getAlbara().getDataProduccio() != null) ? l2.getAlbara().getDataProduccio().toString() : "";
                    return d1.compareTo(d2);
                };
                break;
        }

        if (!asc) comparator = comparator.reversed();

        return linies.stream().sorted(comparator).toList();
    }
}