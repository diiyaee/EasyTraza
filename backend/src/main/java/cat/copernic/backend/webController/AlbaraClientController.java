/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.AlbaraClient;
import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.enums.EstatAlbaraClient;
import cat.copernic.backend.repository.ClientRepository;
import cat.copernic.backend.repository.ProducteRepository;
import cat.copernic.backend.service.AlbaraClientService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/albarans-client")
public class AlbaraClientController {

    @Autowired
    private AlbaraClientService albaraService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProducteRepository producteRepository;

    @GetMapping
    public String getAllAlbaraClients(Model model,
                                     @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataProduccio,
                                     @RequestParam(required = false) String nomClient,
                                     @RequestParam(required = false) String nomUsuari,
                                     @RequestParam(required = false) EstatAlbaraClient estatAlbaraClient,
                                     @RequestParam(required = false, defaultValue = "dataProduccio") String sortField,
                                     @RequestParam(required = false, defaultValue = "true") boolean asc) {

        List<AlbaraClient> albarans = albaraService.filtrar(dataProduccio, nomClient, nomUsuari, estatAlbaraClient);
        albarans = albaraService.ordenarAlbarans(albarans, sortField, asc);

        model.addAttribute("albarans", albarans);
        model.addAttribute("dataProduccio", dataProduccio);
        model.addAttribute("nomClient", nomClient);
        model.addAttribute("nomUsuari", nomUsuari);
        model.addAttribute("estatAlbaraClient", estatAlbaraClient);
        model.addAttribute("sortField", sortField);
        model.addAttribute("asc", asc);

        return "albaransClient";
    }

    @GetMapping("/nouAlbaraClient")
    public String crearForm(Model model) {

        AlbaraClient albara = new AlbaraClient();

        albara.setLinies(new ArrayList<>());
        albara.setDataProduccio(java.time.LocalDate.now());

        model.addAttribute("albara", albara);
        model.addAttribute("mode", "create");

        carregarDades(model);

        return "modAlbaraClient";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {

        AlbaraClient albara = albaraService.getById(id);

        model.addAttribute("albara", albara);
        model.addAttribute("mode", "edit");

        carregarDades(model);

        return "modAlbaraClient";
    }
    
    @PostMapping("/canviar-estat/{id}")
    public String canviarEstat(@PathVariable Long id,
                               RedirectAttributes redirect) {

        try {
            albaraService.canviarEstat(id);
            redirect.addFlashAttribute("ok", "Estado actualizado");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/albarans-client";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("albara") AlbaraClient albara,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirect,
                          @AuthenticationPrincipal cat.copernic.backend.security.UsuariUserDetails userDetails) { // INYECTAMOS EL USUARIO

        if (result.hasErrors()) {
            model.addAttribute("mode", (albara.getId() == null) ? "create" : "edit");
            carregarDades(model);
            return "modAlbaraClient"; // O el nombre que tenga tu HTML de edición
        }

        try {
            if (albara.getId() == null) {
                // 1. Atrapamos al usuario logueado
                Usuari usuariLogueado = userDetails.getUsuari();
                
                // 2. Se lo asignamos al albarán de cliente
                albara.setRegistratPer(usuariLogueado);
                
                albaraService.crear(albara);
            } else {
                albaraService.editar(albara.getId(), albara);
            }

            redirect.addFlashAttribute("ok", "Albarán guardado correctamente");
            return "redirect:/albarans-client";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mode", (albara.getId() == null) ? "create" : "edit");
            carregarDades(model);
            return "modAlbaraClient";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirect) {

        try {

            albaraService.eliminar(id);

            redirect.addFlashAttribute("ok", "Albarán eliminado correctamente");

        } catch (Exception e) {

            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/albarans-client";
    }

    private void carregarDades(Model model) {

        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("productes", producteRepository.findAll());
    }
}