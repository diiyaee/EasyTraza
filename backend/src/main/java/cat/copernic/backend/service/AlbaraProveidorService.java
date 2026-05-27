/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import cat.copernic.backend.DTOs.AlbaraFormDTO;
import cat.copernic.backend.DTOs.AlbaraResponseDTO;
import cat.copernic.backend.DTOs.LiniaDTO;
import cat.copernic.backend.DTOs.OcrAlbaraDTO;
import cat.copernic.backend.DTOs.OcrLiniaDTO;
import cat.copernic.backend.entity.AlbaraProveidor;
import cat.copernic.backend.entity.LiniaProveidor;
import cat.copernic.backend.entity.Lot;
import cat.copernic.backend.entity.Materia;
import cat.copernic.backend.entity.Proveidor;
import cat.copernic.backend.entity.Usuari;
import cat.copernic.backend.enums.EstatLot;
import cat.copernic.backend.repository.AlbaraProveidorRepository;
import cat.copernic.backend.repository.LiniaProveidorRepository;
import cat.copernic.backend.repository.LotRepository;
import cat.copernic.backend.repository.MateriaRepository;
import cat.copernic.backend.repository.ProveidorRepository;
import cat.copernic.backend.repository.UsuariRepository;
import jakarta.transaction.Transactional;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author diyae
 */
@Service
public class AlbaraProveidorService {

    @Autowired
    private AlbaraProveidorRepository albaraRepository;
    
    @Autowired
    private LiniaProveidorRepository liniaRepository;

    @Autowired
    private ProveidorRepository proveidorRepository;

    @Autowired
    private LotRepository lotRepository;
    
    @Autowired
    private MateriaRepository materiaRepository;
    
    @Autowired
    private UsuariRepository usuariRepository;
    
    @Autowired
    private OcrService ocrService;

    @Autowired
    private GroqService groqService;

    @Autowired
    private ObjectMapper objectMapper;

    
    public List<AlbaraProveidor> getAll() {
        return albaraRepository.findAll();
    }

    public AlbaraProveidor getById(Long id) {
        return albaraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Albarán no encontrado"));
    }

    @Transactional
    public AlbaraProveidor crear(AlbaraProveidor albara) {

        try {

            validarBase(albara);

            albara.setDataRecepcio(LocalDate.now());

            Proveidor proveidor = proveidorRepository.findById(albara.getProveidor().getId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            albara.setProveidor(proveidor);

            List<LiniaProveidor> liniesProcessades = processarLinies(albara, proveidor);
            albara.setLinies(liniesProcessades);

            return albaraRepository.save(albara);

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("El número de albarán debe ser único");
        }
    }
    
    @Transactional
    public AlbaraResponseDTO crearDesdeDTO(AlbaraFormDTO dto) {
        
        if (albaraRepository.existsByNumAlbara(dto.getNumAlbara())) {
            throw new RuntimeException("Albarán duplicado");
        }

        if (dto.getLinies() == null || dto.getLinies().isEmpty()) {
            throw new RuntimeException("El albarán debe tener al menos una línea");
        }

        Proveidor proveidor = proveidorRepository.findById(dto.getProveidorId())
            .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

        AlbaraProveidor albara = new AlbaraProveidor();
        albara.setNumAlbara(dto.getNumAlbara());
        albara.setDataRecepcio(dto.getDataRecepcio());
        albara.setProveidor(proveidor);
        
        if (dto.getUsuariId() != null) {
            Usuari usuari = usuariRepository.findById(dto.getUsuariId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            albara.setRegistratPer(usuari);
        }

        List<LiniaProveidor> linies = new ArrayList<>();
        Set<String> controlLots = new HashSet<>();

        for (LiniaDTO l : dto.getLinies()) {

            if (!controlLots.add(l.getNumLot())) {
                throw new RuntimeException("Lote duplicado dentro del albarán");
            }

            Materia materia = materiaRepository.findById(l.getMateriaId())
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            Lot lot = getOrCreateLot(l, materia, proveidor);

            LiniaProveidor linia = new LiniaProveidor();
            linia.setAlbara(albara);
            linia.setLot(lot);
            linia.setQuantitat(l.getQuantitat());
            linia.setUnitats(l.getUnitats() != null ? l.getUnitats().trim() : null);

            linies.add(linia);
        }

        albara.setLinies(linies);

        return new AlbaraResponseDTO(albaraRepository.save(albara));
    }
    
    private Lot getOrCreateLot(
        LiniaDTO l,
        Materia materia,
        Proveidor proveidor
    ) {

        Lot lotExistent = lotRepository
                .findByNumLot(l.getNumLot())
                .orElse(null);

        // ========================================
        // LOTE EXISTENTE
        // ========================================

        if (lotExistent != null) {

            Long existingProveedorId = lotExistent.getProveidor().getId();
            Long existingMateriaId = lotExistent.getMateria().getId();

            if (
                !existingProveedorId.equals(proveidor.getId()) ||
                !existingMateriaId.equals(materia.getId())
            ) {
                throw new RuntimeException(
                    "El lote '" + l.getNumLot() +
                    "' ya existe con otro proveedor o material"
                );
            }

            return lotExistent;
        }

        // ========================================
        // LOTE NUEVO
        // ========================================

        if (l.getDataCaducitat() == null) {
            throw new RuntimeException(
                "La fecha de caducidad es obligatoria en lotes nuevos"
            );
        }

        Lot lot = new Lot();

        lot.setNumLot(l.getNumLot());
        lot.setMateria(materia);
        lot.setProveidor(proveidor);
        lot.setDataCaducitat(l.getDataCaducitat());
        lot.setEstatLot(EstatLot.EN_ESTOC);

        return lotRepository.save(lot);
    }
    
    @Transactional
    public AlbaraProveidor editar(Long id, AlbaraProveidor dades) {

        try {

            AlbaraProveidor albara = getById(id);
            
            validarTodosLotesEnEstoc(albara);

            validarBase(dades);

            Proveidor proveidor = proveidorRepository.findById(dades.getProveidor().getId())
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));

            albara.setNumAlbara(dades.getNumAlbara());
            albara.setDataRecepcio(dades.getDataRecepcio());
            albara.setProveidor(proveidor);

            albara.getLinies().clear();

            List<LiniaProveidor> liniesProcessades = processarLinies(dades, proveidor);
            albara.getLinies().addAll(liniesProcessades);

            return albaraRepository.save(albara);

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("El número de albarán debe ser único");
        }
    }

    private void validarTodosLotesEnEstoc(AlbaraProveidor albara) {

        for (LiniaProveidor linia : albara.getLinies()) {

            if (linia.getLot() == null) {
                continue;
            }

            if (linia.getLot().getEstatLot() != EstatLot.EN_ESTOC) {
                throw new RuntimeException(
                    "No se puede modificar/eliminar el albarán porque hay lotes que no están en estado EN ESTOC"
                );
            }
        }
    }
    
    @Transactional
    public void eliminar(Long id) {

        AlbaraProveidor albara = getById(id);

        validarTodosLotesEnEstoc(albara);

        List<Lot> lots = albara.getLinies()
                .stream()
                .map(LiniaProveidor::getLot)
                .toList();

        albaraRepository.delete(albara);

        for (Lot lot : lots) {

            long usos = liniaRepository.countByLot_Id(lot.getId());

            if (usos == 0) {
                lotRepository.delete(lot);
            }
        }
    }

    private List<LiniaProveidor> processarLinies(AlbaraProveidor albara, Proveidor proveidor) {

        if (albara.getLinies() == null || albara.getLinies().isEmpty()) {
            throw new RuntimeException("El albarán debe tener al menos una línea");
        }

        List<LiniaProveidor> resultat = new ArrayList<>();

        Set<String> controlLots = new HashSet<>();

        for (LiniaProveidor linia : albara.getLinies()) {

            if (linia.getLot() == null) {
                throw new RuntimeException("El lote es obligatorio");
            }

            String numLot = linia.getLot().getNumLot();

            if (numLot == null || numLot.trim().isEmpty()) {
                throw new RuntimeException("El número de lote es obligatorio");
            }

            numLot = numLot.trim();

            if (!controlLots.add(numLot)) {
                throw new RuntimeException("No puedes repetir el mismo lote dentro de un albarán");
            }

            Long materiaId = linia.getLot().getMateria().getId();

            if (materiaId == null) {
                throw new RuntimeException("La materia es obligatoria");
            }

            Lot lot = lotRepository
                    .findByNumLot(numLot)
                    .orElse(null);

            if (lot != null) {

                Long existingProveedorId = lot.getProveidor().getId();
                Long existingMateriaId = lot.getMateria().getId();

                if (!existingProveedorId.equals(proveidor.getId()) ||
                    !existingMateriaId.equals(materiaId)) {

                    throw new RuntimeException(
                            "El lote '" + numLot + "' ya existe con otro proveedor o material y no puede reutilizarse"
                    );
                }

            } else {

                if (linia.getLot().getDataCaducitat() == null) {
                    throw new RuntimeException("La fecha de caducidad es obligatoria para nuevos lotes");
                }

                Materia materia = materiaRepository.findById(materiaId)
                        .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

                lot = new Lot();
                lot.setNumLot(numLot);
                lot.setProveidor(proveidor);
                lot.setMateria(materia);
                lot.setDataCaducitat(linia.getLot().getDataCaducitat());
                lot.setEstatLot(EstatLot.EN_ESTOC);

                lot = lotRepository.save(lot);
            }

            if (linia.getQuantitat() == null || linia.getQuantitat() <= 0) {
                throw new RuntimeException("La cantidad debe ser mayor que 0");
            }


            LiniaProveidor novaLinia = new LiniaProveidor();
            novaLinia.setAlbara(albara);
            novaLinia.setLot(lot);
            novaLinia.setQuantitat(linia.getQuantitat());
            novaLinia.setUnitats(linia.getUnitats().trim());

            resultat.add(novaLinia);
        }

        return resultat;
    }
    
    @Transactional
    public AlbaraProveidor processarImatge(MultipartFile multipartFile) throws Exception {

        File temp = File.createTempFile("ocr-", multipartFile.getOriginalFilename());
        multipartFile.transferTo(temp);

        String textOCR = ocrService.llegirText(temp);

        String respostaGroq = groqService.interpretarText(textOCR);
        
        // ===============================
        // 🔥 DEBUG GROQ RAW RESPONSE
        // ===============================
        System.out.println("=== GROQ JSON ===");
        System.out.println(respostaGroq);

        
        
        String json = respostaGroq;

        // ===============================
        // 🔥 LIMPIEZA ROBUSTA DEL OUTPUT
        // ===============================

        // Quitar bloques markdown si existen
        json = json.replace("```json", "")
                   .replace("```", "")
                   .trim();

        // Extraer SOLO el JSON entre llaves
        int start = json.indexOf("{");
        int end = json.lastIndexOf("}");

        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("No se encontró JSON válido en la respuesta de Groq:\n" + json);
        }

        String jsonLimpio = json.substring(start, end + 1);

        // ===============================
        // PARSE FINAL
        // ===============================
        OcrAlbaraDTO dto = objectMapper.readValue(jsonLimpio, OcrAlbaraDTO.class);

        // ===============================
        // 🔥 DEBUG DTO RESULT
        // ===============================
        System.out.println("=== DTO ===");
        System.out.println("NumAlbara: " + dto.getNumAlbara());
        System.out.println("Proveidor: " + dto.getProveidor());

        if (dto.getLinies() != null) {
            System.out.println("Linies: " + dto.getLinies().size());

            for (OcrLiniaDTO l : dto.getLinies()) {
                System.out.println(" - Lot: " + l.getNumLot());
                System.out.println(" - Materia: " + l.getMateria());
                System.out.println(" - Quantitat: " + l.getQuantitat());
            }
        } else {
            System.out.println("Linies: NULL");
        }
        
        
        
        AlbaraProveidor albara = new AlbaraProveidor();

        // 🔥 IMPORTANTE: inicializar lista
        albara.setLinies(new ArrayList<>());

        albara.setNumAlbara(dto.getNumAlbara());

        if (dto.getDataRecepcio() != null && !dto.getDataRecepcio().isBlank()) {
            albara.setDataRecepcio(LocalDate.parse(dto.getDataRecepcio()));
        }

        Proveidor proveidor = proveidorRepository
                .findByNom(dto.getProveidor())
                .orElseThrow(() ->
                        new RuntimeException("Proveedor no encontrado: " + dto.getProveidor())
                );

        albara.setProveidor(proveidor);

        List<LiniaProveidor> linies = new ArrayList<>();

        for (OcrLiniaDTO l : dto.getLinies()) {

            Materia materia = materiaRepository
                    .findByNom(l.getMateria())
                    .orElseThrow(() ->
                            new RuntimeException("Materia no encontrada: " + l.getMateria())
                    );

            Lot lot = new Lot();
            lot.setNumLot(l.getNumLot());
            lot.setMateria(materia);
            lot.setProveidor(proveidor);

            if (l.getDataCaducitat() != null && !l.getDataCaducitat().isBlank()) {
                lot.setDataCaducitat(LocalDate.parse(l.getDataCaducitat()));
            }

            LiniaProveidor linia = new LiniaProveidor();

            // 🔥 CLAVE PARA THYMELEAF + JPA
            linia.setAlbara(albara);

            linia.setLot(lot);
            linia.setQuantitat(l.getQuantitat());
            linia.setUnitats(l.getUnitats());

            linies.add(linia);
        }

        // 🔥 IMPORTANTE: NO set directo
        albara.getLinies().addAll(linies);

        return albara;
    }

    private void validarBase(AlbaraProveidor albara) {

        if (albara.getNumAlbara() == null || albara.getNumAlbara().isBlank()) {
            throw new RuntimeException("El número de albarán es obligatorio");
        }

        if (albara.getProveidor() == null || albara.getProveidor().getId() == null) {
            throw new RuntimeException("El proveedor es obligatorio");
        }

        boolean existe = albaraRepository.existsByNumAlbara(albara.getNumAlbara());

        if (albara.getId() == null && existe) {
            throw new RuntimeException("El número de albarán debe ser único");
        }

        if (albara.getId() != null) {

            Optional<AlbaraProveidor> existente =
                    albaraRepository.findByNumAlbara(albara.getNumAlbara());

            if (existente.isPresent()
                    && !existente.get().getId().equals(albara.getId())) {

                throw new RuntimeException("El número de albarán debe ser único");
            }
        }
    }
    
    public List<AlbaraProveidor> filtrar(String numAlbara, LocalDate dataRecepcio, String nomProveidor, String nomCognomsUsuari) {
        List<AlbaraProveidor> albarans = albaraRepository.findAll();

        if (numAlbara != null && !numAlbara.isBlank()) {
            albarans = albarans.stream()
                    .filter(a -> a.getNumAlbara() != null && a.getNumAlbara().toLowerCase().contains(numAlbara.toLowerCase()))
                    .toList();
        }
        if (dataRecepcio != null) {
            albarans = albarans.stream()
                    .filter(a -> a.getDataRecepcio() != null && !a.getDataRecepcio().isAfter(dataRecepcio))
                    .toList();
        }
        // Filtro por nombre de proveedor
        if (nomProveidor != null && !nomProveidor.isBlank()) {
            String filter = nomProveidor.toLowerCase();
            albarans = albarans.stream()
                    .filter(a -> a.getProveidor() != null && a.getProveidor().getNom() != null &&
                                 a.getProveidor().getNom().toLowerCase().contains(filter))
                    .toList();
        }
        // Filtro por nombre + apellidos de usuario
        if (nomCognomsUsuari != null && !nomCognomsUsuari.isBlank()) {
            String filter = nomCognomsUsuari.toLowerCase();
            albarans = albarans.stream()
                    .filter(a -> a.getRegistratPer() != null && 
                                 (a.getRegistratPer().getNom() + " " + a.getRegistratPer().getCognoms()).toLowerCase().contains(filter))
                    .toList();
        }
        return albarans;
    }

    public List<AlbaraProveidor> ordenarAlbarans(List<AlbaraProveidor> albarans, String sortField, boolean asc) {
        if (sortField == null) sortField = "dataRecepcio";

        Comparator<AlbaraProveidor> comparator;

        switch (sortField) {
            case "numAlbara":
                comparator = Comparator.comparing(AlbaraProveidor::getNumAlbara, Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "proveidor":
                comparator = Comparator.comparing(a -> a.getProveidor() != null ? a.getProveidor().getNom() : "", Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "registratPer":
                comparator = Comparator.comparing(a -> a.getRegistratPer() != null ? (a.getRegistratPer().getNom() + " " + a.getRegistratPer().getCognoms()) : "", Comparator.nullsLast(String::compareToIgnoreCase));
                break;
            case "dataRecepcio":
            default:
                comparator = Comparator.comparing(AlbaraProveidor::getDataRecepcio, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }

        if (!asc) {
            comparator = comparator.reversed();
        }

        return albarans.stream().sorted(comparator).toList();
    }
}