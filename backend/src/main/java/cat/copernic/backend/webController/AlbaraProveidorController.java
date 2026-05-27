/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.webController;

import cat.copernic.backend.entity.AlbaraProveidor;
import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.repository.MateriaRepository;
import cat.copernic.backend.repository.ProveidorRepository;
import cat.copernic.backend.service.AlbaraProveidorService;
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
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author diyae
 */
@Controller
@RequestMapping("/albarans-proveidor")
public class AlbaraProveidorController {

    @Autowired
    private AlbaraProveidorService albaraService;

    @Autowired
    private ProveidorRepository proveidorRepository;
    
    @Autowired
    private MateriaRepository materiaRepository;

    @GetMapping
    public String llistar(Model model,
                                 @RequestParam(required = false) String numAlbara,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRecepcio,
                                 @RequestParam(required = false) String nomProveidor,
                                 @RequestParam(required = false) String nomCognomsUsuari,
                                 @RequestParam(required = false, defaultValue = "dataRecepcio") String sortField,
                                 @RequestParam(required = false, defaultValue = "true") boolean asc) {

        List<AlbaraProveidor> albarans = albaraService.filtrar(numAlbara, dataRecepcio, nomProveidor, nomCognomsUsuari);
        albarans = albaraService.ordenarAlbarans(albarans, sortField, asc);

        model.addAttribute("albarans", albarans);
        model.addAttribute("numAlbara", numAlbara);
        model.addAttribute("dataRecepcio", dataRecepcio);
        model.addAttribute("nomProveidor", nomProveidor);
        model.addAttribute("nomCognomsUsuari", nomCognomsUsuari);
        model.addAttribute("sortField", sortField);
        model.addAttribute("asc", asc);

        return "albaransProveidor";
    }

    @GetMapping("/nouAlbaraProveidor")
    public String crearForm(Model model) {

        AlbaraProveidor albara = new AlbaraProveidor();

        albara.setLinies(new ArrayList<>());
        albara.setDataRecepcio(java.time.LocalDate.now());

        model.addAttribute("albara", albara);
        model.addAttribute("mode", "create");

        carregarDades(model);

        return "modAlbaraProveidor";
    }
    
    @PostMapping("/ocr")
    public String processarOCR(@RequestParam("imatge") MultipartFile imatge,
                               Model model) {

        try {

            AlbaraProveidor albara =
                    albaraService.processarImatge(imatge);

            model.addAttribute("albara", albara);

            model.addAttribute("mode", "create");

            carregarDades(model);

            return "modAlbaraProveidor";

        } catch (Exception e) {

            model.addAttribute("error",
                    "Error procesando OCR: " + e.getMessage());

            model.addAttribute("albara", new AlbaraProveidor());
            model.addAttribute("mode", "create");

            carregarDades(model);

            return "modAlbaraProveidor";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {

        AlbaraProveidor albara = albaraService.getById(id);

        model.addAttribute("albara", albara);
        model.addAttribute("mode", "edit");

        carregarDades(model);

        return "modAlbaraProveidor";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("albara") AlbaraProveidor albara,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirect,
                          @AuthenticationPrincipal cat.copernic.backend.security.UsuariUserDetails userDetails) {

        if (result.hasErrors()) {
            model.addAttribute("mode", (albara.getId() == null) ? "create" : "edit");
            carregarDades(model);
            return "modAlbaraProveidor";
        }

        try {
            if (albara.getId() == null) {
                Usuari usuariLogueado = userDetails.getUsuari();
                
                albara.setRegistratPer(usuariLogueado);
                
                albaraService.crear(albara);
            } else {
                albaraService.editar(albara.getId(), albara);
            }

            redirect.addFlashAttribute("ok", "Albarán guardado correctamente");
            return "redirect:/albarans-proveidor";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("mode", (albara.getId() == null) ? "create" : "edit");
            carregarDades(model);
            return "modAlbaraProveidor";
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

        return "redirect:/albarans-proveidor";
    }

    private void carregarDades(Model model) {

        model.addAttribute("proveidors", proveidorRepository.findAll());
        model.addAttribute("materies", materiaRepository.findAll());
    }
}