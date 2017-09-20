package com.sidera.meetsfood;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sidera.R;
import com.sidera.authenticator.ServerAuthenticate;
import com.sidera.meetsfood.events.AccountLoadedEvent;
import com.sidera.meetsfood.events.AccountLoadedFailedEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.TokenLoadedEvent;
import com.sidera.meetsfood.utils.AccountHolder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Bus bus = BusProvider.getInstance();
    Account account;
    String token;
    AccountManager manager;
    boolean appStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);

        bus.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("MAIN_ACTIVITY", "RESUME");
        AccountHolder.loadAccount(getApplicationContext(), MainActivity.this);
    }

    @Subscribe
    public void onAccountLoaded(AccountLoadedEvent event) {
        AccountHolder.loadToken(getApplicationContext(), this, event.account);
    }

    @Subscribe
    public void onTokenLoaded(TokenLoadedEvent event) {
        if (appStarted)
            return;

        Intent myIntent = new Intent(getBaseContext(), ChildListActivity.class);
        startActivity(myIntent);
        appStarted = true;
        finish();
    }

    @Subscribe
    public void onAccountLoadFailed(AccountLoadedFailedEvent event) {
        this.finish();
    }
}
