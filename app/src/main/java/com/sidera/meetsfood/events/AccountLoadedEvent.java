package com.sidera.meetsfood.events;


import android.accounts.Account;

public class AccountLoadedEvent {

    public Account account;

    public AccountLoadedEvent(Account account) {
        this.account = account;
    }
}
