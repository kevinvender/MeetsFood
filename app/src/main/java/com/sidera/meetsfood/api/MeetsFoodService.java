package com.sidera.meetsfood.api;


import com.sidera.meetsfood.api.beans.Configurazione;
import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.ContabilitaRowV20;
import com.sidera.meetsfood.api.beans.Contatti;
import com.sidera.meetsfood.api.beans.Disdette;
import com.sidera.meetsfood.api.beans.FiglioDettagli;
import com.sidera.meetsfood.api.beans.FiglioTestata;
import com.sidera.meetsfood.api.beans.Login;
import com.sidera.meetsfood.api.beans.Menu;
import com.sidera.meetsfood.api.beans.ResponseString;
import com.sidera.meetsfood.api.beans.News;
import com.sidera.meetsfood.api.beans.RichiestaAccesso;
import com.sidera.meetsfood.api.beans.Tariffe;
import com.sidera.meetsfood.api.beans.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.FormUrlEncoded;

public interface MeetsFoodService {

//    @POST("/api/V1.0/auth/login")
//    Call<User> loginUser(@Body Login login);
    @POST("/api/V2.0/auth/login")
    Call<User> loginUser(@Body Login login);

//    @GET("/api/V1.0/figli/list/{pagatore}")
//    Call<ArrayList<FiglioTestata>> listChilds(@Path("pagatore") String pagatore);
    @GET("/api/V2.0/figli/list/{pagatore}")
    Call<ArrayList<FiglioTestata>> listChilds(@Path("pagatore") String pagatore);

//    @GET("/api/V1.0/figli/figlio/{pagatore}/{utenza}/{tipologia}")
//    Call<FiglioDettagli> getChild(@Path("pagatore") String pagatore, @Path("utenza") String utenza, @Path("tipologia") String tipologia);
    @GET("/api/V2.0/figli/figlio/{pagatore}/{utenza}/{tipologia}")
    Call<FiglioDettagli> getChild(@Path("pagatore") String pagatore, @Path("utenza") String utenza, @Path("tipologia") String tipologia);

//    @GET("/api/V1.0/figli/conto/{utenza}/{da}/{a}/{tipologia}/{tipo_estrazione}")
//    Call<ArrayList<ContabilitaRow>> getConto(@Path("utenza") String utenza, @Path("da") String da, @Path("a") String a, @Path("tipologia") String tipologia, @Path("tipo_estrazione") String tipo_estrazione);
    @GET("/api/V2.0/figli/conto/{utenza}/{da}/{a}/{tipologia}/{tipo_estrazione}")
    Call<ArrayList<ContabilitaRowV20>> getConto(@Path("utenza") String utenza, @Path("da") String da, @Path("a") String a, @Path("tipologia") String tipologia, @Path("tipo_estrazione") String tipo_estrazione);

//    @GET("/api/V1.0/figli/presenze/{utenza}/{da}/{a}/{tipologia}")
//    Call<ArrayList<ContabilitaRow>> getPresenze(@Path("pagatore") String pagatore, @Path("utenza") String utenza, @Path("tipologia") String tipologia);
    @GET("/api/V2.0/figli/presenze/{utenza}/{da}/{a}/{tipologia}")
    Call<ArrayList<ContabilitaRowV20>> getPresenze(@Path("utenza") String utenza, @Path("da") String da, @Path("a") String a, @Path("tipologia") String tipologia);

//    @GET("/api/V1.0/figli/news/{commessa}/{utenza}")
//    Call<ArrayList<News>> getNews(@Path("commessa") String commessa,@Path("utenza") String utenza);
    @GET("/api/V2.0/figli/news/{commessa}/{utenza}")
    Call<ArrayList<News>> getNews(@Path("commessa") String commessa,@Path("utenza") String utenza);

//    @GET("/api/V1.0/figli/contatti/{commessa}")
//    Call<ArrayList<Contatti>> getContatti(@Path("commessa") String commessa);
    @GET("/api/V2.0/figli/contatti/{commessa}")
    Call<ArrayList<Contatti>> getContatti(@Path("commessa") String commessa);

//    @GET("/api/V1.0/figli/menu/{commessa}/{utenza}/{giorno}")
//    Call<ArrayList<Menu>> getMenu(@Path("commessa") String commessa,@Path("utenza") String utenzaa,@Path("giorno") String giorno);
    @GET("/api/V2.0/figli/menu/{commessa}/{utenza}/{giorno}")
    Call<ArrayList<Menu>> getMenu(@Path("commessa") String commessa,@Path("utenza") String utenza,@Path("giorno") String giorno);

    @GET("/api/V2.0/figli/tariffe/{commessa}/{utenza}/{giorno}")
    Call<ArrayList<Tariffe>> getTariffe(@Path("commessa") String commessa, @Path("utenza") String utenza, @Path("giorno") String giorno);

    @PUT("/api/V2.0/figli/disdetta/set/{commessa}/{utenza}/{giorno}/{tariffa}/pastobianco")
    Call<Disdette>  setPastoInBianco(@Path("commessa") String commessa,@Path("utenza") String utenza,@Path("giorno") String giorno, @Path("tariffa") String tariffa);

    @PUT("/api/V2.0/figli/disdetta/set/{commessa}/{utenza}/{giorno}/{tariffa}/normale")
    Call<Disdette>  setDisdetta(@Path("commessa") String commessa, @Path("utenza") String utenza, @Path("giorno") String giorno, @Path("tariffa") String tariffa);

    @PUT("/api/V2.0/figli/disdetta/rev/{commessa}/{utenza}/{giorno}")
    Call<Disdette>  revDisdetta(@Path("commessa") String commessa,@Path("utenza") String utenza,@Path("giorno") String giorno);

    @GET("/api/V2.0/figli/disdetta/info/{commessa}/{utenza}/{giorno}")
    Call<ArrayList<Disdette>> infoDisdetta(@Path("commessa") String commessa,@Path("utenza") String utenza,@Path("giorno") String giorno);

//    @POST("/api/V1.0/pwd/change")
//    @FormUrlEncoded
//    Call<ResponseString> changePassword(@Field("requestString") String requestString);
    @POST("/api/V2.0/pwd/change")
    @FormUrlEncoded
    Call<ResponseString> changePassword(@Field("requestString") String requestString);

//    @GET("/api/V1.0/config/{commessa}")
//    Call<ArrayList<Configurazione>> getConfig(@Path("commessa") String commessa);
    @GET("/api/V2.0/config/{commessa}")
    Call<ArrayList<Configurazione>> getConfig(@Path("commessa") String commessa);

//    @POST("/api/V1.0/auth/request")
//    Call<ResponseString> richiestaAccesso(@Body RichiestaAccesso richiestaAccesso);
    @POST("/api/V2.0/auth/request")
    Call<ResponseString> richiestaAccesso(@Body RichiestaAccesso richiestaAccesso);




}
