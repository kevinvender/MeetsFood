package com.sidera.meetsfood.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sidera.meetsfood.MeetsFoodApplication;
import com.sidera.meetsfood.api.beans.JWTPayload;
import com.sidera.meetsfood.api.beans.User;
import com.sidera.meetsfood.events.AccountLoadedEvent;
import com.sidera.meetsfood.events.AccountLoadedFailedEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.TokenLoadedEvent;
import com.squareup.otto.Bus;

import static com.sidera.authenticator.AccountGeneral.ACCOUNT_TYPE;
import static com.sidera.authenticator.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

import java.io.IOException;
import java.io.Serializable;

import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by Lorenzo on 13/06/2016.
 */
public class AccountHolder {
    public static AccountHolder instance;

    private Context context;
    private Account account;
    private User user;
    private String token;

    public static AccountHolder getInstance() {
        if (instance == null) {
            SharedPreferences preferences = MeetsFoodApplication.getInstance().getSharedPreferences("ACCOUNT_HOLDER", 0);
            instance = new AccountHolder(preferences.getString("TOKEN", null));
        }
        return instance;
    }

    public Account getAccount() {
        return account;
    }

    public String getToken() {
        return token;
    }

    public User getUser() { return user; }

    public static void loadAccount(Context context, Activity activity) {
        final Bus bus = BusProvider.getInstance();
        final AccountManager manager = AccountManager.get(context);
        final Account[] accounts = manager.getAccountsByType(ACCOUNT_TYPE);

        if (accounts.length == 0) {
            manager.addAccount(ACCOUNT_TYPE, AUTHTOKEN_TYPE_FULL_ACCESS, null, null, activity, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        Bundle bnd = future.getResult();
                        Account[] accounts = manager.getAccountsByType(ACCOUNT_TYPE);
                        Account account = accounts[0];
                        bus.post(new AccountLoadedEvent(account));
                        Log.d(this.getClass().getSimpleName(), "AddNewAccount Bundle is " + bnd);
                    } catch (Exception e) {
                        e.printStackTrace();
                        bus.post(new AccountLoadedFailedEvent());
                    }
                }
            }, null);
        } else {
            Account account = accounts[0];
            bus.post(new AccountLoadedEvent(account));
        }
    }

    public static void loadToken(Context context, Activity activity, Account account) {
        final Bus bus = BusProvider.getInstance();
        final AccountManager manager = AccountManager.get(context);
        final Account acc = account;

        final AccountManagerFuture<Bundle> future = manager.getAuthToken(account, AUTHTOKEN_TYPE_FULL_ACCESS, null, activity, null, null);
        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                final Intent res = new Intent();
                try {
                    res.putExtras(future.getResult());
                } catch (Exception e) {
                    e.printStackTrace();
                    bus.post(new AccountLoadedFailedEvent());
                }
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                String token = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                AccountHolder.initialize(acc, token);
                bus.post(new TokenLoadedEvent(token));
            }
        }.execute();
    }

    public static void initialize(Account account, String token) {
        instance = new AccountHolder(account, token);

        SharedPreferences preferences = MeetsFoodApplication.getInstance().getSharedPreferences("ACCOUNT_HOLDER", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TOKEN", token);
        editor.commit();
    }

    private AccountHolder(Account account, String token) {
        this.account = account;
        this.token = token;
        Log.e("USER", String.valueOf(this.account));
        Log.e("USER", this.token);

        String[] strings = token.split(".");
        String ut = String.valueOf(token.substring(token.indexOf(".")+1, token.lastIndexOf(".")));
        ObjectMapper m = new ObjectMapper();
        try {
            JWTPayload jwtPayload = m.readValue(Base64.decode(ut, Base64.DEFAULT), JWTPayload.class);
            this.user = jwtPayload.user;
            //Log.e("USER", this.user.display_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AccountHolder(String token) {
        this.token = token;

        String[] strings = token.split(".");
        String ut = String.valueOf(token.substring(token.indexOf(".")+1, token.lastIndexOf(".")));
        ObjectMapper m = new ObjectMapper();
        try {
            JWTPayload jwtPayload = m.readValue(Base64.decode(ut, Base64.DEFAULT), JWTPayload.class);
            this.user = jwtPayload.user;
            Log.e("USER", this.user.display_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
