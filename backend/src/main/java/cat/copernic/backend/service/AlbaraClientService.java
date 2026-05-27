/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.AlbaraClient;
import cat.copernic.backend.entity.Client;
import cat.copernic.backend.entity.LiniaClient;
import cat.copernic.backend.entity.Producte;
import cat.copernic.backend.enums.EstatAlbaraClient;
import cat.copernic.backend.repository.AlbaraClientRepository;
import cat.copernic.backend.repository.ClientRepository;
import cat.copernic.backend.repository.ProducteRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author diyae
 */
@Service
public class AlbaraClientService {

    @Autowired
    private AlbaraClientRepository albaraRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProducteRepository producteRepository;

    public List<AlbaraClient> getAll() {
        return albaraRepository.findAll();
    }

    public AlbaraClient getById(Long id) {
        return albaraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Albarán no encontrado"));
    }

    @Transactional
    public AlbaraClient crear(AlbaraClient albara) {

        validarBase(albara);

        Client client = clientRepository.findById(albara.getClient().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        albara.setClient(client);

        albara.setEstatAlbaraClient(EstatAlbaraClient.NO_LLIURAT);

        List<LiniaClient> liniesProcessades = processarLinies(albara);
        albara.setLinies(liniesProcessades);

        return albaraRepository.save(albara);
    }

    @Transactional
    public AlbaraClient editar(Long id, AlbaraClient dades) {

        AlbaraClient albara = getById(id);

        validarAlbaraNoLliurat(albara);

        validarBase(dades);

        Client client = clientRepository.findById(dades.getClient().getId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        albara.setClient(client);
        albara.setDataProduccio(dades.getDataProduccio());

        albara.getLinies().clear();

        List<LiniaClient> liniesProcessades = processarLinies(dades);
        albara.getLinies().addAll(liniesProcessades);

        return albaraRepository.save(albara);
    }

    @Transactional
    public void eliminar(Long id) {

        AlbaraClient albara = getById(id);

        validarAlbaraNoLliurat(albara);

        albaraRepository.delete(albara);
    }

    private void validarAlbaraNoLliurat(AlbaraClient albara) {

        if (albara.getEstatAlbaraClient() != EstatAlbaraClient.NO_LLIURAT) {
            throw new RuntimeException(
                    "No se puede modificar/eliminar el albarán porque ya ha sido entregado"
            );
        }
    }
    
    @Transactional
    public AlbaraClient canviarEstat(Long id) {

        AlbaraClient albara = getById(id);

        if (albara.getEstatAlbaraClient() == EstatAlbaraClient.LLIURAT) {
            throw new RuntimeException("No se puede cambiar un albarán ya entregado");
        }

        albara.setEstatAlbaraClient(EstatAlbaraClient.LLIURAT);

        return albaraRepository.save(albara);
    }

    private List<LiniaClient> processarLinies(AlbaraClient albara) {

        if (albara.getLinies() == null || albara.getLinies().isEmpty()) {
            throw new RuntimeException("El albarán debe tener al menos una línea");
        }

        List<LiniaClient> resultat = new ArrayList<>();
        Set<Long> controlProductes = new HashSet<>();

        for (LiniaClient linia : albara.getLinies()) {

            if (linia.getProducte() == null || linia.getProducte().getId() == null) {
                throw new RuntimeException("El producto es obligatorio");
            }

            Long producteId = linia.getProducte().getId();

            if (!controlProductes.add(producteId)) {
                throw new RuntimeException("No puedes repetir el mismo producto en un albarán");
            }

            Producte producte = producteRepository.findById(producteId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (linia.getQuantitat() == null || linia.getQuantitat() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor que 0");
            }

            LiniaClient novaLinia = new LiniaClient();
            novaLinia.setAlbara(albara);
            novaLinia.setProducte(producte);
            novaLinia.setQuantitat(linia.getQuantitat());

            resultat.add(novaLinia);
        }

        return resultat;
    }

    private void validarBase(AlbaraClient albara) {

        if (albara.getClient() == null || albara.getClient().getId() == null) {
            throw new RuntimeException("El cliente es obligatorio");
        }

        if (albara.getDataProduccio() == null) {
            throw new RuntimeException("La fecha de producción es obligatoria");
        }

        Optional<AlbaraClient> existente =
                albaraRepository.findByClient_IdAndDataProduccio(
                        albara.getClient().getId(),
                        albara.getDataProduccio()
                );

        if (albara.getId() == null && existente.isPresent()) {
            throw new RuntimeException("Ya existe un albarán para este cliente en esa fecha");
        }

        if (albara.getId() != null && existente.isPresent()
                && !existente.get().getId().equals(albara.getId())) {

            throw new RuntimeException("Ya existe un albarán para este cliente en esa fecha");
        }
    }
    
    public List<AlbaraClient> filtrar(LocalDate dataProduccio, String nomClient, String nomUsuari, EstatAlbaraClient estat) {
        List<AlbaraClient> albarans = albaraRepository.findAll();

        if (dataProduccio != null) {
            albarans = albarans.stream()
                    .filter(a -> a.getDataProduccio() != null && a.getDataProduccio().isEqual(dataProduccio))
                    .toList();
        }

        if (nomClient != null && !nomClient.isBlank()) {
            String filter = nomClient.toLowerCase();
            albarans = albarans.stream()
                    .filter(a -> a.getClient() != null && 
                                 (a.getClient().getNom() + " " + a.getClient().getCognoms()).toLowerCase().contains(filter))
                    .toList();
        }

        if (nomUsuari != null && !nomUsuari.isBlank()) {
            String filter = nomUsuari.toLowerCase();
            albarans = albarans.stream()
                    .filter(a -> a.getRegistratPer() != null && 
                                 (a.getRegistratPer().getNom() + " " + a.getRegistratPer().getCognoms()).toLowerCase().contains(filter))
                    .toList();
        }

        if (estat != null) {
            albarans = albarans.stream()
                    .filter(a -> a.getEstatAlbaraClient() == estat)
                    .toList();
        }

        return albarans;
    }

    public List<AlbaraClient> ordenarAlbarans(List<AlbaraClient> albarans, String sortField, boolean asc) {
        if (sortField == null) sortField = "dataProduccio";

        Comparator<AlbaraClient> comparator;

        switch (sortField) {
            case "client":
                comparator = Comparator.comparing(a -> a.getClient() != null ? (a.getClient().getNom() + " " + a.getClient().getCognoms()) : "", Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "registratPer":
                comparator = Comparator.comparing(a -> a.getRegistratPer() != null ? (a.getRegistratPer().getNom() + " " + a.getRegistratPer().getCognoms()) : "", Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "estat":
                comparator = Comparator.comparing(a -> a.getEstatAlbaraClient().name());
                break;
            case "dataProduccio":
            default:
                comparator = Comparator.comparing(AlbaraClient::getDataProduccio, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }

        if (!asc) {
            comparator = comparator.reversed();
        }

        return albarans.stream().sorted(comparator).toList();
    }
}