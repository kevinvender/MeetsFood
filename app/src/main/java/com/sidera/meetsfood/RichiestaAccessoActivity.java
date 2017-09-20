package com.sidera.meetsfood;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sidera.R;
import com.sidera.authenticator.ApiGeneral;
import com.sidera.meetsfood.api.MeetsFoodService;
import com.sidera.meetsfood.api.beans.ResponseString;
import com.sidera.meetsfood.api.beans.RichiestaAccesso;
import com.sidera.meetsfood.events.ApiErrorEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.utils.AccountHolder;
import com.sidera.meetsfood.utils.UserAgentInterceptor;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RichiestaAccessoActivity extends AppCompatActivity {

    Bus bus = BusProvider.getInstance();
    ProgressDialog progressDialog;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private EditText iEmailText;
    private EditText iCellText;
    private EditText iCardText;
    private EditText iPanText;

    public RichiestaAccessoActivity(){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_richiesta_accesso);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_richiesta_attivazione);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iEmailText = (EditText) findViewById(R.id.input_email_accesso);
        iCellText = (EditText) findViewById(R.id.input_cell_accesso);
        iCardText = (EditText) findViewById(R.id.input_cardid_accesso);
        iPanText = (EditText) findViewById(R.id.input_pan_accesso);

        Button mPwdChangeButton = (Button) findViewById(R.id.richiesta_attivazione_button);
        mPwdChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                richiediAccesso();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(RichiestaAccessoActivity.this).create();
        alertDialog.setTitle("Attenzione");
        alertDialog.setMessage("Per richiedere l'accesso devi essere sicuro/a che l'amministrazione o la società di ristorazione abbia previsto l'utlizzo dell'app MeetsFood. " +
                "In caso affermativo devi essere registrato/a come adulto. " +
                "La casella di posta e il numero di PAN, o in alternativa il Card ID, del figlio servono per verificare la tua registrazione come adulto." +
                "Se inserisci anche il numero di cellulare i dati di accesso ti verranno inviati via SMS, in caso contrario all'indirizzo di posta indicato.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        bus.register(this);
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
       // progressDialog.dismiss();
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_password_change, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void richiediAccesso(){
        String emailAccesso = iEmailText.getText().toString().trim();
        String cellAccesso = iCellText.getText().toString().trim();
        String cardAccesso = iCardText.getText().toString().trim();
        String panAccesso = iPanText.getText().toString().trim();

        boolean cancel = false;
        if(TextUtils.isEmpty(emailAccesso) && TextUtils.isEmpty(cellAccesso)){
            cancel = true;
        }

        if(TextUtils.isEmpty(cardAccesso) && TextUtils.isEmpty(panAccesso)){
            cancel = true;
        }

        if (cancel) {
            iEmailText.requestFocus();
        }else{
            final RichiestaAccesso richiestaAccesso = new RichiestaAccesso();
            richiestaAccesso.mail = emailAccesso;
            richiestaAccesso.telefono = cellAccesso;
            richiestaAccesso.card_id = cardAccesso;
            richiestaAccesso.pan = panAccesso;

            new AsyncTask<RichiestaAccesso, Void, ResponseString>() {

                @Override
                protected ResponseString doInBackground(RichiestaAccesso... params) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                    ResponseString responseString = new ResponseString();
                    Bundle data = new Bundle();
                    try {
                        RichiestaAccesso ra = params[0];

                        Log.e("ACCESSO","MAIL:"+ra.mail);
                        Log.e("ACCESSO","CELL:"+ra.telefono);
                        Log.e("ACCESSO","CARD:"+ra.card_id);
                        Log.e("ACCESSO","PAN:"+ra.pan);
                        responseString = richiediAccesso(ra);

                    } catch (Exception e) {
                        responseString.response = getString(R.string.msg_richiesta_accesso_nok);
                        Log.e("ACCESSO",">"+e.getMessage());
                    }

                    return responseString;
                }

                @Override
                protected void onPostExecute(ResponseString responseString) {
                    try {
                        if (responseString.response.equals("OK")) {
                            Toast.makeText(RichiestaAccessoActivity.this, getString(R.string.msg_richiesta_accesso_ok), Toast.LENGTH_SHORT).show();
                            finish();
                        }else if (responseString.response.indexOf("NOK_COMM") >= 0) {
                            Toast.makeText(RichiestaAccessoActivity.this, getString(R.string.msg_richiesta_accesso_nok_commessa), Toast.LENGTH_SHORT).show();
                        } else if (responseString.response.equals("NOK")) {
                            Toast.makeText(RichiestaAccessoActivity.this, getString(R.string.msg_richiesta_accesso_nok), Toast.LENGTH_SHORT).show();
                        }
                        else if (responseString.response.equals("EXCEPTION")) {
                            Toast.makeText(RichiestaAccessoActivity.this, getString(R.string.msg_exception), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RichiestaAccessoActivity.this, responseString.response, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex){
                        Toast.makeText(RichiestaAccessoActivity.this, getString(R.string.msg_exception), Toast.LENGTH_SHORT).show();
                    }

                }
            }.execute(richiestaAccesso);
        }

    }

    private ResponseString richiediAccesso(RichiestaAccesso richiestaAccesso){
        String sUA = MeetsFoodApplication.sApplicationName + "/" + MeetsFoodApplication.sPackageName + " (" + MeetsFoodApplication.sVersion + ")";
        httpClient.addInterceptor(new UserAgentInterceptor(sUA));
        OkHttpClient client = httpClient.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiGeneral.API_SERVER)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        MeetsFoodService service = retrofit.create(MeetsFoodService.class);
        Call<ResponseString> respCall = service.richiestaAccesso(richiestaAccesso);

        ResponseString responseString = new ResponseString();
        try {
            Response<ResponseString> userResponse = respCall.execute();
            responseString = userResponse.body();
        } catch (IOException e) {
            Log.e("ACCESSO", ">" + e.getMessage());

            responseString.response = getString(R.string.msg_exception);
        }

        return responseString;
    }

}
