package com.sidera.meetsfood;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sidera.R;
import com.sidera.authenticator.AccountGeneral;
import com.sidera.meetsfood.adapters.ChildrenAdapter;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.Configurazione;
import com.sidera.meetsfood.api.beans.Dummy;
import com.sidera.meetsfood.api.beans.FiglioTestata;
import com.sidera.meetsfood.api.beans.Login;
import com.sidera.meetsfood.data.Child;
import com.sidera.meetsfood.data.ChildrenContent;
import com.sidera.meetsfood.events.ApiErrorEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.ListLoadedEvent;
import com.sidera.meetsfood.events.LoadConfigEvent;
import com.sidera.meetsfood.events.LoadListEvent;
import com.sidera.meetsfood.events.ReloadInterfaceEvent;
import com.sidera.meetsfood.utils.AccountHolder;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.sidera.authenticator.AccountGeneral.ACCOUNT_TYPE;
import static com.sidera.authenticator.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import static java.security.AccessController.getContext;


public class ChildListActivity extends AppCompatActivity {

    Bus bus = BusProvider.getInstance();
    ProgressDialog progressDialog;
    ChildrenAdapter adapter;
    ArrayList<FiglioTestata> childList = new ArrayList<>();
    boolean listLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_list);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_launcher);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_child_list);

        bus.post(new LoadConfigEvent(AccountHolder.getInstance().getUser().commessa));

        //aggiunta menu
        toolbar.inflateMenu(R.menu.toolbar_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.menu_exit:
                        ChildListActivity.this.finish();

                        return true;
                    case R.id.menu_logout:
                        AccountManager am = AccountManager.get( ChildListActivity.this);
                        Account[] accounts = am.getAccounts();

                        for(int i = 0;i<accounts.length;i++) {
                            Account acc = accounts[i];

                            if(acc.name.equals(AccountHolder.getInstance().getUser().username)){
                                Log.e("ACCOUNT", acc.toString());
                                am.removeAccount(acc, null, null);
                                //am.clearPassword(acc);
                                ChildListActivity.this.finish();

                                Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(myIntent);
                            }
                        }
                       // Toast.makeText(ChildListActivity.this,"Logout",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_change_pwd:
                        Intent myIntent = new Intent(getBaseContext(), PasswordChangeActivity.class);
                        startActivity(myIntent);

                        return true;
                    case R.id.menu_privacy:
                        ArrayList<Configurazione> confCommessa = ApiService.configCommessa;
                        for(int i = 0;i<confCommessa.size();i++){
                            Configurazione conf = confCommessa.get(i);
                            Log.e("CONFIG",conf.codice+":"+conf.valore);
                            if(conf.codice.equals("PRIVACY_URL") && !conf.valore.equals("")){
                                String url = conf.valore;
                                Intent iBrowser = new Intent(Intent.ACTION_VIEW);
                                iBrowser.setData(Uri.parse(url));
                                startActivity(iBrowser);
                            }

                        }

                        return true;
                }

                return false;
            }
        });

        /*LinearLayout footer = (LinearLayout) findViewById(R.id.footer);
        TextView tv_saldo_footer = (TextView) findViewById(R.id.totale_saldo_value);
        tv_saldo_footer.setText("€ 0,00");*/



        ListView listView = (ListView) findViewById(R.id.child_list);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.child_list_progressBar);
        listView.setEmptyView(progressBar);
        adapter = new ChildrenAdapter(this, childList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected(childList.get(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    public void onItemSelected(FiglioTestata figlio) {
        Intent detailIntent = new Intent(this, ChildDetailActivity.class);
        detailIntent.putExtra(ChildDetailActivity.ARG_FIGLIO, figlio);
        startActivity(detailIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        bus.register(this);
        if (!listLoaded) {
            progressDialog.show();
            bus.post(new LoadListEvent(AccountHolder.getInstance().getUser().pagatore));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        bus.unregister(this);
        progressDialog.dismiss();
    }


    @Subscribe
    public void onListLoadedEvent(ListLoadedEvent event) {
        for (FiglioTestata d: event.list) {
            Log.e("DUMMY", d.display_name);
        }
        progressDialog.dismiss();
        listLoaded = true;
        childList = event.list;
        adapter.setData(childList);
        /*Double saldoTot = 0d;
        for (FiglioTestata f: childList) {
            saldoTot += f.saldo;
        }
        TextView tv_saldo_footer = (TextView) findViewById(R.id.totale_saldo_value);
        LinearLayout footer = (LinearLayout) findViewById(R.id.footer);
        DecimalFormat df = new DecimalFormat("#.00");

        tv_saldo_footer.setText("€ " + df.format(saldoTot));
        if (saldoTot >= 0) {
            footer.setBackgroundColor(getResources().getColor(R.color.footer_green));
        } else {
            footer.setBackgroundColor(getResources().getColor(R.color.footer_red));
        }*/
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        progressDialog.dismiss();
    }

    @Subscribe
    public void onReloadInterface(ReloadInterfaceEvent evt) {
        progressDialog.show();
        bus.post(new LoadListEvent(AccountHolder.getInstance().getUser().pagatore));
    }
}
