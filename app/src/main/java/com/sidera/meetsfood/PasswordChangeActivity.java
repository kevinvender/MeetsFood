package com.sidera.meetsfood;


import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sidera.R;
import com.sidera.authenticator.ApiGeneral;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.MeetsFoodService;
import com.sidera.meetsfood.api.beans.ResponseString;
import com.sidera.meetsfood.api.beans.User;
import com.sidera.meetsfood.events.ApiErrorEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.ChangePasswordEvent;
import com.sidera.meetsfood.events.LoadListEvent;
import com.sidera.meetsfood.events.PasswordChangedEvent;
import com.sidera.meetsfood.utils.AccountHolder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.text.Normalizer;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class PasswordChangeActivity extends AppCompatActivity {

    Bus bus = BusProvider.getInstance();
    ProgressDialog progressDialog;
    private EditText ePwdOldText;
    private EditText ePwdNewText;
    private EditText ePwdNew1Text;
    private String sPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_pwd_change);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ePwdOldText = (EditText) findViewById(R.id.input_pwd_old);
        ePwdOldText.requestFocus();

        ePwdNewText = (EditText) findViewById(R.id.input_pwd_new);
        ePwdNew1Text = (EditText) findViewById(R.id.input_pwd_new_1);

        Button mPwdChangeButton = (Button) findViewById(R.id.change_pwd_button);
        mPwdChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password_change, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void changePassword(){
        String oldPwd = ePwdOldText.getText().toString();
        String newPwd = ePwdNewText.getText().toString();
        String newPwd1 = ePwdNew1Text.getText().toString();

        boolean cancel = false;
        AccountManager am = AccountManager.get(PasswordChangeActivity.this);
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(oldPwd)){
            ePwdOldText.setError(getString(R.string.error_field_required));
            cancel = true;
        }else if(!oldPwd.equals(am.getPassword(AccountHolder.getInstance().getAccount()))){
            ePwdOldText.setError(getString(R.string.error_incorrect_password));
            cancel = true;
        }

        if (TextUtils.isEmpty(newPwd)){
            ePwdNewText.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (TextUtils.isEmpty(newPwd1)){
            ePwdNew1Text.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if(!cancel && !newPwd.equals(newPwd1)){
            ePwdNewText.setError(getString(R.string.error_incorrect_password));
            ePwdNew1Text.setError(getString(R.string.error_incorrect_password));
            cancel = true;
        }

        if(!cancel && !isPasswordValid(newPwd1)) {
            ePwdNewText.setError(getString(R.string.error_invalid_password));
            ePwdNew1Text.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            ePwdOldText.requestFocus();
        }else{
            //chiamata a cambio password
            String requestString = AccountHolder.getInstance().getUser().username + "§§" + oldPwd + "§§" + newPwd;
            sPwd = newPwd;

            bus.register(this);

            progressDialog.show();
            Log.e("PASSWORD", "ChangePasswordEvent");

            bus.post(new ChangePasswordEvent(requestString));

        }

    }

    @Subscribe
   public void onPasswordChangedEvent(PasswordChangedEvent event) {
        Log.e("PASSWORD", "onPasswordChangedEvent");
        progressDialog.dismiss();

        AccountManager am = AccountManager.get(PasswordChangeActivity.this);
        if(event.responseString.response.equals("OK")) {
            Toast.makeText(PasswordChangeActivity.this, getString(R.string.msg_cambio_password_ok), Toast.LENGTH_SHORT).show();
            am.setPassword(AccountHolder.getInstance().getAccount(), sPwd);
            finish();
        }else if(event.responseString.response.equals("NOK")) {
            Toast.makeText(PasswordChangeActivity.this, getString(R.string.msg_cambio_password_nok), Toast.LENGTH_SHORT).show();
        }else if(event.responseString.response.equals("EXCEPTION")) {
            Toast.makeText(PasswordChangeActivity.this, getString(R.string.msg_exception), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 7;
    }
}
