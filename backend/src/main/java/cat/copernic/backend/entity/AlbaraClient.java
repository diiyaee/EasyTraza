/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.entity;

import cat.copernic.backend.enums.EstatAlbaraClient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class AlbaraClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de producción es obligatoria")
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataProduccio;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "usuari_id") 
    private Usuari registratPer;
    
    @Enumerated(EnumType.STRING)
    private EstatAlbaraClient estatAlbaraClient = EstatAlbaraClient.NO_LLIURAT;
    
    @OneToMany(mappedBy = "albara", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LiniaClient> linies = new ArrayList<>();

    public AlbaraClient() {
    }

    public void addLinia(LiniaClient linia) {
        linies.add(linia);
        linia.setAlbara(this);
    }

    public void removeLinia(LiniaClient linia) {
        linies.remove(linia);
        linia.setAlbara(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataProduccio() {
        return dataProduccio;
    }

    public void setDataProduccio(LocalDate dataProduccio) {
        this.dataProduccio = dataProduccio;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
    
    public Usuari getRegistratPer() {
        return registratPer;
    }

    public void setRegistratPer(Usuari registratPer) {
        this.registratPer = registratPer;
    }

    public EstatAlbaraClient getEstatAlbaraClient() {
        return estatAlbaraClient;
    }

    public void setEstatAlbaraClient(EstatAlbaraClient estatAlbaraClient) {
        this.estatAlbaraClient = estatAlbaraClient;
    }
    
    public List<LiniaClient> getLinies() {
        return linies;
    }

    public void setLinies(List<LiniaClient> linies) {
        this.linies = linies;
    }
}