package contatos.esc.com.contatos.api;

import java.util.List;

import contatos.esc.com.contatos.model.Contato;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiContactInterface {
    @GET("contacts")
    Call<List<Contato>> getAll();

    @GET("contact/{id}")
    Call<Contato> getById(@Path("id") int id);

    @DELETE("contact/{id}")
    Call<Contato> delete(@Path("id") int id);

    @FormUrlEncoded
    @POST("contact")
    Call<Contato> create(@Field("name") String name, @Field("fone") String fone);

    @FormUrlEncoded
    @PUT("contact/{id}")
    Call<Contato> update(@Path("id") int id, @Field("name") String name, @Field("fone") String fone);
}
