/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.apiService;

import cat.copernic.backend.entity.Usuari;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 *
 * @author diyae
 */
public interface UsuariApiService {
    
    final String BASE_URL = "/rest/usuaris";
    
    @GET(BASE_URL)
    Call<List<Usuari>> findAll();

}
