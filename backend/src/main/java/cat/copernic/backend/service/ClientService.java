/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.entity.Client;
import cat.copernic.backend.repository.ClientRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author diyae
 */
@Service
public class ClientService {
    
    @Autowired
    private ClientRepository clientRepository;
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
    }
    
    private String normalitzar(String text) {
        if (text == null) return null;

        return text
                .trim()
                .replaceAll("\\s+", " ");
    }
    
    public Client crearClient(Client c) {

        String nom = normalitzar(c.getNom());
        String cognoms = normalitzar(c.getCognoms());
        String nif = c.getNif() != null ? c.getNif().trim().toUpperCase() : null;
        String telefon = c.getTelefon() != null ? c.getTelefon().trim() : null;
        String adreca = c.getAdreca() != null ? c.getAdreca().trim() : null;

        if (clientRepository.existsByNomIgnoreCaseAndCognomsIgnoreCase(nom, cognoms)) {
            throw new IllegalArgumentException("Ya existe un cliente con este nombre y apellidos.");
        }

        if (nif != null && clientRepository.existsByNif(nif)) {
            throw new IllegalArgumentException("Ya existe un cliente con este NIF.");
        }
        
        if (nif != null && clientRepository.existsByTelefon(telefon)) {
            throw new IllegalArgumentException("Ya existe un cliente con este teléfono.");
        }
        
        if (nif != null && clientRepository.existsByAdreca(adreca)) {
            throw new IllegalArgumentException("Ya existe un cliente con esta dirección.");
        }

        c.setNom(nom);
        c.setCognoms(cognoms);
        c.setNif(nif);
        c.setTelefon(telefon);
        c.setAdreca(adreca);

        if (telefon == null || telefon.isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }

        if (adreca == null || adreca.isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }

        return clientRepository.save(c);
    }
    
    public Client editarClient(Long id, Client dades) {

        String nom = normalitzar(dades.getNom());
        String cognoms = normalitzar(dades.getCognoms());
        String nif = dades.getNif() != null ? dades.getNif().trim().toUpperCase() : null;
        String telefon = dades.getTelefon() != null ? dades.getTelefon().trim() : null;
        String adreca = dades.getAdreca() != null ? dades.getAdreca().trim() : null;

        if (clientRepository.existsByNomIgnoreCaseAndCognomsIgnoreCaseAndIdNot(nom, cognoms, id)) {
            throw new IllegalArgumentException("Ya existe un cliente con este nombre y apellidos.");
        }
        
        if (clientRepository.existsByTelefonAndIdNot(telefon, id)) {
            throw new IllegalArgumentException("Ya existe un cliente con este teléfono.");
        }
        
        if (clientRepository.existsByAdrecaAndIdNot(adreca, id)) {
            throw new IllegalArgumentException("Ya existe un cliente con esta dirección.");
        }

        if (nif != null) {
            Optional<Client> existente = clientRepository.findByNif(nif);
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe un cliente con este NIF.");
            }
        }

        Client c = getClientById(id);

        c.setNom(nom);
        c.setCognoms(cognoms);
        c.setNif(nif);
        c.setTelefon(telefon);
        c.setAdreca(adreca);
        c.setObservacions(dades.getObservacions());

        // Validaciones básicas extra
        if (telefon == null || telefon.isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }

        if (adreca == null || adreca.isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }

        return clientRepository.save(c);
    }
    
    public void eliminarClient(Long id) {
        try {
            clientRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT, 
                "No se ha podido eliminar el cliente porque tiene albaranes asociados."
            );
        }
    }
    
    public List<Client> filtrar(String nomCognoms, String nif, String telefon, String adreca) {
        List<Client> clients = clientRepository.findAll();

        // Filtro combinado de nombre y apellidos
        if (nomCognoms != null && !nomCognoms.isBlank()) {
            String filter = nomCognoms.toLowerCase();
            clients = clients.stream()
                    .filter(c -> (c.getNom() != null && c.getNom().toLowerCase().contains(filter)) ||
                                 (c.getCognoms() != null && c.getCognoms().toLowerCase().contains(filter)))
                    .toList();
        }

        if (nif != null && !nif.isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getNif() != null && c.getNif().toLowerCase().contains(nif.toLowerCase()))
                    .toList();
        }
        if (telefon != null && !telefon.isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getTelefon() != null && c.getTelefon().toLowerCase().contains(telefon.toLowerCase()))
                    .toList();
        }
        if (adreca != null && !adreca.isBlank()) {
            clients = clients.stream()
                    .filter(c -> c.getAdreca() != null && c.getAdreca().toLowerCase().contains(adreca.toLowerCase()))
                    .toList();
        }
        return clients;
    }

    public List<Client> ordenarClients(List<Client> clients, String sortField, boolean asc) {
        if (sortField == null) sortField = "nom";

        Comparator<Client> comparator;

        switch (sortField) {
            case "nif":
                comparator = Comparator.comparing(Client::getNif, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "telefon":
                comparator = Comparator.comparing(Client::getTelefon, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "adreca":
                comparator = Comparator.comparing(Client::getAdreca, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "nom":
            default:
                // Ordenar por nombre
                comparator = Comparator.comparing(Client::getNom, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
        }

        if (!asc) {
            comparator = comparator.reversed();
        }

        return clients.stream().sorted(comparator).toList();
    }
}
