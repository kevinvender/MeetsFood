package com.sidera.authenticator;

import android.os.Build;
import android.util.Log;

import com.sidera.meetsfood.MeetsFoodApplication;
import com.sidera.meetsfood.api.MeetsFoodService;
import com.sidera.meetsfood.api.beans.Login;
import com.sidera.meetsfood.api.beans.User;
import com.sidera.meetsfood.utils.UserAgentInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Lorenzo on 20/05/2016.
 */
public class ServerAuthenticate {
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public String userSignIn(String email, String pass, String authType) throws Exception {
        String sUA = MeetsFoodApplication.sApplicationName + "/" + MeetsFoodApplication.sPackageName + " (" + MeetsFoodApplication.sVersion + ")";
        httpClient.addInterceptor(new UserAgentInterceptor(sUA));

        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiGeneral.API_SERVER)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        String authtoken = null;

        String sOperatingSystem = "android:"+Build.VERSION.RELEASE;
        sOperatingSystem+= ":sdk="+Build.VERSION.SDK_INT;

        MeetsFoodService service = retrofit.create(MeetsFoodService.class);

        Call<User> userCall = service.loginUser(new Login(email, pass, sOperatingSystem));
        Response<User> userResponse = userCall.execute();

        User user = userResponse.body();

        if(user.username == null) throw new Exception("Impossibile effettuare l'accesso. Controllare i dati inseriti.");
    /*    Log.e("USER", user.toString());
        Log.e("USER", user.display_name);
        Log.e("USER", user.username);
        Log.e("USER", user.token);*/
        authtoken = user.token;

        return authtoken;
    }
}
