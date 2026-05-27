/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author diyae
 */
@Entity
public class AlbaraProveidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de albarán es obligatorio")
    @Column(nullable = false, unique = true)
    private String numAlbara;

    @NotNull(message = "La fecha de recepción es obligatoria")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataRecepcio;

    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne
    @JoinColumn(name = "proveidor_id", nullable = false)
    private Proveidor proveidor;
    
    @ManyToOne
    @JoinColumn(name = "usuari_id") 
    private Usuari registratPer;

    @OneToMany(mappedBy = "albara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiniaProveidor> linies = new ArrayList<>();

    public AlbaraProveidor() {
    }

    public void addLinia(LiniaProveidor linia) {
        linies.add(linia);
        linia.setAlbara(this);
    }

    public void removeLinia(LiniaProveidor linia) {
        linies.remove(linia);
        linia.setAlbara(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumAlbara() {
        return numAlbara;
    }

    public void setNumAlbara(String numAlbara) {
        this.numAlbara = numAlbara;
    }

    public LocalDate getDataRecepcio() {
        return dataRecepcio;
    }

    public void setDataRecepcio(LocalDate dataRecepcio) {
        this.dataRecepcio = dataRecepcio;
    }

    public Proveidor getProveidor() {
        return proveidor;
    }

    public void setProveidor(Proveidor proveidor) {
        this.proveidor = proveidor;
    }
    
    public Usuari getRegistratPer() {
        return registratPer;
    }

    public void setRegistratPer(Usuari registratPer) {
        this.registratPer = registratPer;
    }

    public List<LiniaProveidor> getLinies() {
        return linies;
    }

    public void setLinies(List<LiniaProveidor> linies) {
        this.linies = linies;
    }
}