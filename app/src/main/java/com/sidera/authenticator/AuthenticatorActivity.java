package com.sidera.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sidera.R;
import com.sidera.meetsfood.RichiestaAccessoActivity;


public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    private final String TAG = this.getClass().getSimpleName();

    // UI references.
    private EditText emailView;
    private EditText passwordView;
    private ProgressDialog progressDialog;
    private View loginFormView;

    private AccountManager accountManager;
    private String mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accountManager = AccountManager.get(getBaseContext());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
//        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
//        if (mAuthTokenType == null)
//            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        // Set up the login form.

        emailView = (EditText) findViewById(R.id.input_email);

        passwordView = (EditText) findViewById(R.id.input_password);
        loginFormView = findViewById(R.id.login_form);
        //emailView.getBackground().setColorFilter(getResources().getColor(R.color.bg_white), PorterDuff.Mode.SRC_ATOP);
       //emailView.setBackgroundColor(0x80FFFFFF);//setBackgroundColor( R.color.green);//.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.bg_white), PorterDuff.Mode.SRC_ATOP);
        //emailView.getBackground().setColorFilter((0xFF000000), PorterDuff.Mode.SRC_IN);
        //emailView.getBackground().setColorFilter((0x80FFFFFF), PorterDuff.Mode.SRC_ATOP);
        //emailView.setBackgroundColor(Color.argb((int) 0.8,255, 255, 255));
       // emailView.setText("MeetsFood");
       // passwordView.setText("Sidemo2016!");

        if (accountName != null) {
            emailView.setText(accountName);
        }

        Button bRichiediAccesso = (Button) findViewById(R.id.link_richiedi_accesso);
        bRichiediAccesso.setPaintFlags(bRichiediAccesso.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Log.e("ACCESSO", bRichiediAccesso.getText().toString());
        bRichiediAccesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ACCESSO", "link_richiedi_accesso::onClick");
                Intent myIntent = new Intent(getBaseContext(), RichiestaAccessoActivity.class);
                startActivity(myIntent);
            }
        });



        findViewById(R.id.email_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
//        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
//                // and return them in setAccountAuthenticatorResult(). See finishLogin().
//                Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
//                signup.putExtras(getIntent().getExtras());
//                startActivityForResult(signup, REQ_SIGNUP);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public void submit() {

        final String userName = emailView.getText().toString();
        final String userPass = passwordView.getText().toString();

        //Log.e("LOGIN", userName);
        //Log.e("LOGIN", userPass);

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

//         Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(userPass) && !isPasswordValid(userPass)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            passwordView.requestFocus();
            return;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            emailView.setError(getString(R.string.error_field_required));
            emailView.requestFocus();
            return;
        }

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showProgress(true);
                    }
                });
                Log.d("meetsFood", TAG + "> Started authenticating");

                String authtoken = null;
                Bundle data = new Bundle();
                try {
                    authtoken = new ServerAuthenticate().userSignIn(userName, userPass, mAuthTokenType);

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(PARAM_USER_PASS, userPass);

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("AUTHENTICATION","EX::"+e.getMessage());
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                showProgress(false);

                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_LONG).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        Log.d("meetsFood", TAG + "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("meetsFood", TAG + "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, accountPassword, null);
            accountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Log.d("meetsFood", TAG + "> finishLogin > setPassword");
            accountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }


    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            if (show)
                    progressDialog.show();
            else {
                progressDialog.dismiss();
            }
        } else {
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            if (show)
                progressDialog.show();
            else {
                progressDialog.dismiss();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
