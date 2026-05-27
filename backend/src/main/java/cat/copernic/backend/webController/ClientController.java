/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.Client;
import cat.copernic.backend.service.ClientService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/clients")
public class ClientController {
    
    @Autowired
    private ClientService clientService;
    
    @GetMapping
    public String getAllClients(Model model,
                                @RequestParam(required = false) String nomCognoms,
                                @RequestParam(required = false) String nif,
                                @RequestParam(required = false) String telefon,
                                @RequestParam(required = false) String adreca,
                                @RequestParam(required = false, defaultValue = "nom") String sortField,
                                @RequestParam(required = false, defaultValue = "true") boolean asc) {

        List<Client> clients = clientService.filtrar(nomCognoms, nif, telefon, adreca);
        clients = clientService.ordenarClients(clients, sortField, asc);

        model.addAttribute("clients", clients);
        model.addAttribute("nomCognoms", nomCognoms);
        model.addAttribute("nif", nif);
        model.addAttribute("telefon", telefon);
        model.addAttribute("adreca", adreca);
        model.addAttribute("sortField", sortField);
        model.addAttribute("asc", asc);

        return "clients";
    }
    
    @GetMapping("/nouClient")
    public String mostrarFormCrear(Model model) {

        model.addAttribute("client", new Client());
        model.addAttribute("mode", "create");

        return "modClient";
    }
    
    @GetMapping("/modClient/{id}")
    public String mostrarFormModificar(@PathVariable Long id, Model model) {

        Client c = clientService.getClientById(id);

        model.addAttribute("client", c);
        model.addAttribute("mode", "edit");
        
        return "modClient";
    }
    
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("client") Client client,
                          BindingResult result,
                          Model model) {

        if (result.hasErrors()) {
            model.addAttribute("client", client);
            model.addAttribute("mode", client.getId() == null ? "create" : "edit");
            return "modClient";
        }
        
        try {

            if (client.getNif() != null &&
                !client.getNif().matches("(^[0-9]{8}[A-HJ-NP-TV-Z]$)|(^[A-HJ-NP-TV-Z][0-9]{7,8}[0-9A-J]$)")) {

                throw new IllegalArgumentException("El NIF/DNI no es válido.");
            }

            if (client.getNif() != null && client.getNif().trim().length() > 9)
                throw new IllegalArgumentException("El NIF no puede tener más de 9 caracteres.");

            if (client.getNom() != null && client.getNom().length() > 100)
                throw new IllegalArgumentException("El nombre no puede tener más de 100 caracteres.");

            if (client.getCognoms() != null && client.getCognoms().length() > 100)
                throw new IllegalArgumentException("Los apellidos no pueden tener más de 100 caracteres.");

            if (client.getCognoms() != null &&
                !client.getCognoms().matches("^[a-zA-ZÀ-ÿ\\s]+$"))
                throw new IllegalArgumentException("Los apellidos solo pueden contener letras.");

            if (client.getTelefon() != null && !client.getTelefon().matches("^[0-9]{9}$"))
                throw new IllegalArgumentException("El teléfono debe tener 9 dígitos.");

            if (client.getAdreca() != null && client.getAdreca().length() > 200)
                throw new IllegalArgumentException("La dirección no puede tener más de 200 caracteres.");

            if (client.getObservacions() != null && client.getObservacions().length() > 500)
                throw new IllegalArgumentException("Las observaciones no pueden superar los 500 caracteres.");

            if (client.getId() == null) {

                clientService.crearClient(client);

            } else {

                clientService.editarClient(client.getId(), client);
            }

            return "redirect:/clients";

        } catch (Exception e) {

            model.addAttribute("missatge", e.getMessage());
            model.addAttribute("client", client);

            return "modClient";
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, Model model) {
        try {
            clientService.eliminarClient(id);
            return "redirect:/clients";

        } catch (ResponseStatusException e) {
            model.addAttribute("missatge", e.getReason());
            model.addAttribute("clients", clientService.getAllClients()); 
            return "clients";

        } catch (Exception e) {
            model.addAttribute("missatge", "Error inesperado al intentar eliminar el cliente.");
            model.addAttribute("clients", clientService.getAllClients());
            return "clients";
        }
    }
}