package com.sidera.meetsfood.api;


import android.util.Log;

import com.sidera.authenticator.ApiGeneral;
import com.sidera.meetsfood.MeetsFoodApplication;
import com.sidera.meetsfood.api.beans.Configurazione;
import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.ContabilitaRowV20;
import com.sidera.meetsfood.api.beans.Contatti;
import com.sidera.meetsfood.api.beans.Disdette;
import com.sidera.meetsfood.api.beans.FiglioDettagli;
import com.sidera.meetsfood.api.beans.FiglioTestata;
import com.sidera.meetsfood.api.beans.Menu;
import com.sidera.meetsfood.api.beans.ResponseString;
import com.sidera.meetsfood.api.beans.News;
import com.sidera.meetsfood.api.beans.Tariffe;
import com.sidera.meetsfood.api.exceptions.Error4xxException;
import com.sidera.meetsfood.events.ApiErrorEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.ChangePasswordEvent;
import com.sidera.meetsfood.events.ChildLoadedEvent;
import com.sidera.meetsfood.events.ConfigLoadedEvent;
import com.sidera.meetsfood.events.ContattiLoadedEvent;
import com.sidera.meetsfood.events.DisdettaEvent;
import com.sidera.meetsfood.events.DisdettaInfoLoadedEvent;
import com.sidera.meetsfood.events.DisdettaSettedEvent;
import com.sidera.meetsfood.events.EstrattoContoLoadedEvent;
import com.sidera.meetsfood.events.ListLoadedEvent;
import com.sidera.meetsfood.events.LoadChildEvent;
import com.sidera.meetsfood.events.LoadConfigEvent;
import com.sidera.meetsfood.events.LoadContattiEvent;
import com.sidera.meetsfood.events.LoadDisdettaInfoEvent;
import com.sidera.meetsfood.events.LoadEstrattoContoEvent;
import com.sidera.meetsfood.events.LoadListEvent;
import com.sidera.meetsfood.events.LoadMenuEvent;
import com.sidera.meetsfood.events.LoadNewsEvent;
import com.sidera.meetsfood.events.LoadPresenzeEvent;
import com.sidera.meetsfood.events.LoadTariffeEvent;
import com.sidera.meetsfood.events.MenuLoadedEvent;
import com.sidera.meetsfood.events.NewsLoadedEvent;
import com.sidera.meetsfood.events.PasswordChangedEvent;
import com.sidera.meetsfood.events.PastoInBiancoEvent;
import com.sidera.meetsfood.events.PastoInBiancoSettedEvent;
import com.sidera.meetsfood.events.PresenzeLoadedEvent;
import com.sidera.meetsfood.events.RevDisdettaEvent;
import com.sidera.meetsfood.events.RevDisdettaSettedEvent;
import com.sidera.meetsfood.events.TariffeLoadedEvent;
import com.sidera.meetsfood.utils.AccountHolder;
import com.sidera.meetsfood.utils.UserAgentInterceptor;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ApiService {
    private static ApiService instance;
    private MeetsFoodService service;
    private Bus bus = BusProvider.getInstance();
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static FiglioTestata figlioTestata = null;
    public static FiglioDettagli figlioDettagli = null;
    public static ArrayList<ContabilitaRowV20> estrattoConto = null;
    public static ArrayList<ContabilitaRowV20> presenze = null;
    public static ArrayList<News> news = null;
    public static ArrayList<Menu> menu = null;
    public static ArrayList<Tariffe> tariffe = null;
    public static ArrayList<Disdette> disdettaInfo = null;
    public static Disdette disdettaRev = null;
    public static Disdette disdetta = null;
    public static Disdette pastoInBianco = null;
    public static ArrayList<Contatti> contatti = null;
    public static ArrayList<Configurazione> configCommessa = null;
    public static ResponseString responseString = null;
    public static Date presenzeMonth = new Date();
    public static Date dataMenu = new Date();

    public static ApiService getInstance() {
        return instance;
    }

    public ApiService() {
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", "Bearer " + AccountHolder.getInstance().getToken())
                            .method(original.method(), original.body());

                //MeetsFood/com.sidera.MeetsFood (60914; iOS 9.3.0)
                //\(executable)/\(bundle) (\(version); \(osNameVersion))”
               // requestBuilder.addHeader("User-Agent",MeetsFoodApplication.sApplicationName+"/"+ MeetsFoodApplication.sPackageName+" ("+MeetsFoodApplication.sVersion+")");

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

//        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                .tlsVersions(TlsVersion.TLS_1_2)
//                .cipherSuites(
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
//                .build();

        Retrofit retrofit = null;
        try {
            SSLSocketFactory NoSSLv3Factory = new NoSSLv3SocketFactory(new URL(ApiGeneral.API_SERVER));
            httpClient.sslSocketFactory(NoSSLv3Factory);

            String sUA = MeetsFoodApplication.sApplicationName + "/" + MeetsFoodApplication.sPackageName + " (" + MeetsFoodApplication.sVersion + ")";
            httpClient.addInterceptor(new UserAgentInterceptor(sUA));

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiGeneral.API_SERVER)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        service = retrofit.create(MeetsFoodService.class);

        instance = this;
    }

    @Subscribe
    public void onLoadListEvent(LoadListEvent event) {
        service.listChilds(event.pagatore).enqueue(new Callback<ArrayList<FiglioTestata>>() {
            @Override
            public void onResponse(Call<ArrayList<FiglioTestata>> call, Response<ArrayList<FiglioTestata>> response) {
                if (response.isSuccessful())
                    bus.post(new ListLoadedEvent(response.body()));
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<FiglioTestata>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadChildEvent(LoadChildEvent event) {
        figlioDettagli = new FiglioDettagli();
        service.getChild(event.pagatore, event.utenza, event.tipologia).enqueue(new Callback<FiglioDettagli>() {
            @Override
            public void onResponse(Call<FiglioDettagli> call, Response<FiglioDettagli> response) {
                if (response.isSuccessful()) {
                    bus.post(new ChildLoadedEvent(response.body()));
                    figlioDettagli = response.body();
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<FiglioDettagli> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadPresenzeEvent(LoadPresenzeEvent event) {
        presenze = new ArrayList<>();
        service.getPresenze(event.utenza, event.da, event.a, event.tipologia).enqueue(new Callback<ArrayList<ContabilitaRowV20>>() {
            @Override
            public void onResponse(Call<ArrayList<ContabilitaRowV20>> call, Response<ArrayList<ContabilitaRowV20>> response) {
                if (response.isSuccessful()) {
                    presenze = response.body();
                    bus.post(new PresenzeLoadedEvent(response.body()));
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<ContabilitaRowV20>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadEstrattoContoEvent(LoadEstrattoContoEvent event) {
        estrattoConto = new ArrayList<>();
        //2016-06-01
        service.getConto(event.utenza, event.da, event.a, event.tipologia,event.tipo_estrazione).enqueue(new Callback<ArrayList<ContabilitaRowV20>>() {
            @Override
            public void onResponse(Call<ArrayList<ContabilitaRowV20>> call, Response<ArrayList<ContabilitaRowV20>> response) {
                Log.e("ESTRATTO_CONTO","response:"+response.isSuccessful());
                if (response.isSuccessful()) {
                    bus.post(new EstrattoContoLoadedEvent(response.body()));
                    estrattoConto = response.body();
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<ContabilitaRowV20>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadNewsEvent(LoadNewsEvent event) {
        service.getNews(event.commessa, event.utenza).enqueue(new Callback<ArrayList<News>>() {
            @Override
            public void onResponse(Call<ArrayList<News>> call, Response<ArrayList<News>> response) {
                if (response.isSuccessful()) {
                    bus.post(new NewsLoadedEvent(response.body()));
                    news = response.body();
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<News>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadContattiEvent(LoadContattiEvent event) {
        service.getContatti(event.commessa).enqueue(new Callback<ArrayList<Contatti>>() {
            @Override
            public void onResponse(Call<ArrayList<Contatti>> call, Response<ArrayList<Contatti>> response) {
                if (response.isSuccessful()) {
                    bus.post(new ContattiLoadedEvent(response.body()));
                    contatti = response.body();
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<Contatti>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onChangePasswordEvent(ChangePasswordEvent event) {
        service.changePassword(event.requestString).enqueue(new Callback<ResponseString>() {

            @Override
            public void onResponse(Call<ResponseString> call, Response<ResponseString> response) {
                if (response.isSuccessful()) {
                    bus.post(new PasswordChangedEvent(response.body()));
                } else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ResponseString> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadConfigEvent(LoadConfigEvent event) {
        configCommessa = new ArrayList<Configurazione>();
        service.getConfig(event.commessa).enqueue(new Callback<ArrayList<Configurazione>>() {
            @Override
            public void onResponse(Call<ArrayList<Configurazione>> call, Response<ArrayList<Configurazione>> response) {
                if (response.isSuccessful()) {
                    configCommessa = response.body();
                    bus.post(new ConfigLoadedEvent(response.body()));
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<Configurazione>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadMenuEvent(LoadMenuEvent event) {
        service.getMenu(event.commessa, event.utenza, event.data).enqueue(new Callback<ArrayList<Menu>>() {
            @Override
            public void onResponse(Call<ArrayList<Menu>> call, Response<ArrayList<Menu>> response) {
                if (response.isSuccessful()) {
                    menu = response.body();
                    bus.post(new MenuLoadedEvent(response.body()));
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<Menu>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadTariffeEvent(LoadTariffeEvent event) {
        service.getTariffe(event.commessa, event.utenza, event.data).enqueue(new Callback<ArrayList<Tariffe>>() {
            @Override
            public void onResponse(Call<ArrayList<Tariffe>> call, Response<ArrayList<Tariffe>> response) {
                if (response.isSuccessful()) {
                    tariffe = response.body();
                    bus.post(new TariffeLoadedEvent(response.body()));
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<Tariffe>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onLoadDisdettainfoEvent(LoadDisdettaInfoEvent event) {
        disdettaInfo = new ArrayList<Disdette>();
        service.infoDisdetta(event.commessa, event.utenza, event.data).enqueue(new Callback<ArrayList<Disdette>>() {
            @Override
            public void onResponse(Call<ArrayList<Disdette>> call, Response<ArrayList<Disdette>> response) {
                if (response.isSuccessful()) {
                    disdettaInfo = response.body();
                    bus.post(new DisdettaInfoLoadedEvent(response.body()));
                }
                else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<ArrayList<Disdette>> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onPastoInBiancoEvent(PastoInBiancoEvent event) {
        pastoInBianco = new Disdette();
        service.setPastoInBianco(event.commessa, event.utenza, event.data, event.tariffa).enqueue(new Callback<Disdette>() {

            @Override
            public void onResponse(Call<Disdette> call, Response<Disdette> response) {
                if (response.isSuccessful()) {
                    pastoInBianco = response.body();
                    bus.post(new PastoInBiancoSettedEvent(response.body()));
                } else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<Disdette> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onDisdettaEvent(DisdettaEvent event) {
        disdetta = new Disdette();
        service.setDisdetta(event.commessa, event.utenza, event.data, event.tariffa).enqueue(new Callback<Disdette>() {

            @Override
            public void onResponse(Call<Disdette> call, Response<Disdette> response) {
                if (response.isSuccessful()) {
                    disdetta = response.body();
                    bus.post(new DisdettaSettedEvent(response.body()));
                } else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<Disdette> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }

    @Subscribe
    public void onRevDisdettaEvent(RevDisdettaEvent event) {
        disdettaRev = new Disdette();
        service.revDisdetta(event.commessa, event.utenza, event.data).enqueue(new Callback<Disdette>() {

            @Override
            public void onResponse(Call<Disdette> call, Response<Disdette> response) {
                if (response.isSuccessful()) {
                    disdettaRev = response.body();
                    bus.post(new RevDisdettaSettedEvent(response.body()));
                } else
                    bus.post(new ApiErrorEvent(new Error4xxException(response.code())));
            }

            @Override
            public void onFailure(Call<Disdette> call, Throwable t) {
                bus.post(new ApiErrorEvent(t));
            }
        });
    }


}
