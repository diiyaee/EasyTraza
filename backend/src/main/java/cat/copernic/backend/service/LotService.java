/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.Lot;
import cat.copernic.backend.enums.EstatLot;
import cat.copernic.backend.repository.LotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author diyae
 */
@Service
public class LotService {

    @Autowired
    private LotRepository lotRepository;

    public List<Lot> getAllLots() {
        return lotRepository.findAll();
    }

    public Lot getLotById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
    }

    public Lot editarLot(Long id, Lot dades) {

        Lot l = getLotById(id);

        if (l.getEstatLot() != EstatLot.EN_ESTOC) {
            throw new RuntimeException("Solo se pueden editar lotes en estado EN ESTOC");
        }

        l.setNumLot(dades.getNumLot());
        l.setMateria(dades.getMateria());
        l.setProveidor(dades.getProveidor());
        l.setDataCaducitat(dades.getDataCaducitat());

        return lotRepository.save(l);
    }
    
    public boolean existeLotObertMateixaMateria(Long materiaId) {
        return lotRepository
                .findByMateria_IdAndEstatLot(materiaId, EstatLot.OBERT)
                .isPresent();
    }
    
    public boolean obrirLot(Long id, boolean forcar) {

        Lot l = getLotById(id);

        Optional<Lot> existent = lotRepository
                .findByMateria_IdAndEstatLot(l.getMateria().getId(), EstatLot.OBERT);

        if (existent.isPresent() && !forcar) {
            return false; // 🚨 conflicto detectado
        }

        // si hay otro abierto, lo cerramos
        existent.ifPresent(lotObert -> {
            lotObert.setEstatLot(EstatLot.ACABAT);
            lotObert.setDataAcabament(LocalDateTime.now());
            lotRepository.save(lotObert);
        });

        // abrimos el nuevo
        l.setEstatLot(EstatLot.OBERT);
        l.setDataObertura(LocalDateTime.now());

        lotRepository.save(l);

        return true;
    }

    public Lot acabarLot(Long id) {

        Lot l = getLotById(id);

        l.setEstatLot(EstatLot.ACABAT);
        l.setDataAcabament(LocalDateTime.now());

        return lotRepository.save(l);
    }
    
    public List<Lot> filtrar(String numLot,
                             EstatLot estatLot,
                             Long materiaId,
                             LocalDate dataCaducitat) {

        List<Lot> lots = lotRepository.findAll();

        if (numLot != null && !numLot.isBlank()) {
            lots = lots.stream()
                    .filter(l -> l.getNumLot() != null &&
                            l.getNumLot().toLowerCase().contains(numLot.toLowerCase()))
                    .toList();
        }

        if (estatLot != null) {
            lots = lots.stream()
                    .filter(l -> l.getEstatLot() == estatLot)
                    .toList();
        }

        if (materiaId != null) {
            lots = lots.stream()
                    .filter(l -> l.getMateria() != null &&
                            l.getMateria().getId().equals(materiaId))
                    .toList();
        }

        if (dataCaducitat != null) {
            lots = lots.stream()
                    .filter(l -> l.getDataCaducitat() != null &&
                            !l.getDataCaducitat().isAfter(dataCaducitat))
                    .toList();
        }

        return lots;
    }
    
    public List<Lot> ordenarLots(List<Lot> lots, String sortField, boolean asc) {
        
        if (sortField == null) sortField = "dataCaducitat";

        Comparator<Lot> comparator;

        switch (sortField) {
            case "numLot":
                comparator = Comparator.comparing(Lot::getNumLot, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "materia":
                comparator = Comparator.comparing(
                        l -> l.getMateria() != null ? l.getMateria().getNom() : "",
                        Comparator.nullsLast(String::compareToIgnoreCase)
                );
                break;
            case "dataCaducitat":
            default:
                comparator = Comparator.comparing(Lot::getDataCaducitat, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }

        if (!asc) {
            comparator = comparator.reversed();
        }

        return lots.stream().sorted(comparator).toList();
    }
}