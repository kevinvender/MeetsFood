package com.sidera.authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AccountGeneral {

    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "com.sidera.meetsfood";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Meetsfood account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to a Meetsfood account";

//    public static final Account getAccount(final Activity activity) {
//        AccountManager accountManager = AccountManager.get(activity);
//        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
//        if (accounts.length == 0) {
//            accountManager.addAccount(ACCOUNT_TYPE, AUTHTOKEN_TYPE_FULL_ACCESS, null, null, activity, new AccountManagerCallback<Bundle>() {
//                @Override
//                public void run(AccountManagerFuture<Bundle> future) {
//                    try {
//                        Bundle bnd = future.getResult();
//                    } catch (OperationCanceledException e) {
//                        activity.finish();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        activity.finish();
//                    }
//                }
//            }, null);
//        } else {
//            return accounts[0];
//        }
//    }

//    public static final ServerAuthenticate sServerAuthenticate = new ParseComServerAuthenticate();
}