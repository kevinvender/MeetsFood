package com.sidera.meetsfood;

import android.accounts.AccountManager;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.sidera.authenticator.AccountGeneral;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.exceptions.Error4xxException;
import com.sidera.meetsfood.events.AccountLoadedEvent;
import com.sidera.meetsfood.events.AccountLoadedFailedEvent;
import com.sidera.meetsfood.events.ApiErrorEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.ReloadInterfaceEvent;
import com.sidera.meetsfood.events.TokenLoadedEvent;
import com.sidera.meetsfood.utils.AccountHolder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.net.SocketTimeoutException;


public class MeetsFoodApplication extends Application {

    private Bus bus = BusProvider.getInstance();
    private static MeetsFoodApplication instance;
    public static String sPackageName;
    public static String sApplicationName;
    public static String sVersion;
    public static String sAppVersion;

    private int errorCounter = 0;

    public static MeetsFoodApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Resources appR = getApplicationContext().getResources();
        CharSequence txt = appR.getText(appR.getIdentifier("app_name","string", getApplicationContext().getPackageName()));

        sApplicationName = txt.toString().replaceAll(" ", "");
        sPackageName = getApplicationContext().getPackageName();

        try {
            sAppVersion = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
            sAppVersion+= " ";
        } catch (PackageManager.NameNotFoundException e) {
            sAppVersion = "";
        }

        sVersion =  sAppVersion+"android:"+ Build.VERSION.RELEASE;
        sVersion+= ":sdk="+Build.VERSION.SDK_INT;


     //   txt = appR.getText(appR.getIdentifier("app_name","string", getApplicationContext().getApplicationInfo()));
      //  sPackageName = txt.toString();

        bus.register(this);
        bus.register(new ApiService());
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        event.error.printStackTrace();

        try {
            throw event.error;
        } catch (SocketTimeoutException ste) {
            Toast.makeText(getApplicationContext(), "Impossibile contattare il server. Verificare connessione.", Toast.LENGTH_LONG).show();
        } catch (Error4xxException exc) {
            if (exc.errorCode == 401) {
                AccountManager.get(getApplicationContext()).invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, AccountHolder.getInstance().getToken());
                if (AccountHolder.getInstance().getAccount() != null) {
                    if (errorCounter < 3) {
                        AccountHolder.loadToken(getApplicationContext(), null, AccountHolder.getInstance().getAccount());
                        bus.post(new ReloadInterfaceEvent());
                        errorCounter++;
                    } else {
                        errorCounter = 0;
                        Toast.makeText(getApplicationContext(), "Errore in fase di autenticazione. Accesso negato.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            }
        } catch (Throwable throwable) {
            Toast.makeText(getApplicationContext(), "Something went wrong, please try again.", Toast.LENGTH_LONG).show();
        }
    }
}
