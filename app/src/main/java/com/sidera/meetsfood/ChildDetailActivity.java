package com.sidera.meetsfood;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sidera.R;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.Configurazione;
import com.sidera.meetsfood.api.beans.FiglioDettagli;
import com.sidera.meetsfood.api.beans.FiglioTestata;
import com.sidera.meetsfood.events.ApiErrorEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.ChangePasswordEvent;
import com.sidera.meetsfood.events.ChildLoadedEvent;
import com.sidera.meetsfood.events.ConfigLoadedEvent;
import com.sidera.meetsfood.events.ContattiLoadedEvent;
import com.sidera.meetsfood.events.EstrattoContoLoadedEvent;
import com.sidera.meetsfood.events.LoadChildEvent;
import com.sidera.meetsfood.events.LoadConfigEvent;
import com.sidera.meetsfood.events.LoadContattiEvent;
import com.sidera.meetsfood.events.LoadEstrattoContoEvent;
import com.sidera.meetsfood.events.LoadListEvent;
import com.sidera.meetsfood.events.LoadMenuEvent;
import com.sidera.meetsfood.events.LoadNewsEvent;
import com.sidera.meetsfood.events.LoadPresenzeEvent;
import com.sidera.meetsfood.events.MenuLoadedEvent;
import com.sidera.meetsfood.events.NewsLoadedEvent;
import com.sidera.meetsfood.events.PresenzeLoadedEvent;
import com.sidera.meetsfood.events.ReloadInterfaceEvent;
import com.sidera.meetsfood.utils.AccountHolder;
//import com.sidera.meetsfood.utils.PickImageActivity;
import com.sidera.meetsfood.utils.ConnectivityUtils;
import com.sidera.meetsfood.utils.Utility;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ChildDetailActivity extends AppCompatActivity {

    Bus bus = BusProvider.getInstance();
    public static final String ARG_FIGLIO = "FIGLIO";
    public static final String CONTENT_FILE_PROVIDER = "com.sidera.meetsfood.provider";
    ProgressDialog progressDialog;
    public FiglioTestata child;
    private FiglioDettagli childDetails;
    private boolean detailsLoaded = false;
    private SectionPagerAdapter adapter;
    private File filename = null;
    private File filename_tmp = null;
    private File filename_icon = null;
    private Uri imageUri;
    public boolean showMenu = true;
    public boolean canPay = false;
    public int nrTab = 6;
    private int PICK_IMAGE_REQUEST = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    //data da a estratto conto
    private String data_da;
    private String data_a;
    private String tipo_estrazione = "1";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String strImagePath = "no image selected";
    public static Context context;
    public static boolean decrescente;
    public WebView webView;
    public Button webButton;
    public TextView webTitle;
    public String urlPagamenti = "";

   // private Menu selectionMenu;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_details);

        //-----------------------------------------------------
        //modifiche
        context = ChildDetailActivity.this;

        //showMenu = false;
        //----------------------------------------------
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");

        if (getIntent().hasExtra(ARG_FIGLIO)) {
            child = (FiglioTestata) getIntent().getExtras().getSerializable(ARG_FIGLIO);
            ApiService.figlioTestata = child;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(child.display_name);
        ImageView image = (ImageView) findViewById(R.id.header_image);
        image.setImageDrawable(null);
        Picasso.with(this).load(new File(getFilesDir(), "/profile" + child.utenza + ".png")).memoryPolicy(MemoryPolicy.NO_CACHE).into(image);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        bus.post(new LoadConfigEvent(child.id_commessa));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setupWithViewPager(viewPager);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //get del parametro decrescente

        getDecrescente();
    }

    @Override
    protected void onResume() {
        super.onResume();

        filename_tmp = new File(getFilesDir(), "profile_tmp" + child.utenza + ".png");
        filename = new File(getFilesDir(), "profile" + child.utenza + ".png");
        imageUri = FileProvider.getUriForFile(this, CONTENT_FILE_PROVIDER, filename_tmp);
        filename_icon = new File(getFilesDir(), "profile_icon" + child.utenza + ".png");

        bus.register(this);
        ApiService.figlioTestata = child;
        if (!detailsLoaded) {
            progressDialog.show();

            if (data_da == null || data_a == null) {
                Calendar cal = Calendar.getInstance();
                data_a = sdf.format(cal.getTime());

                getDaData();
                getDecrescente();
            }

            bus.post(new LoadChildEvent(child.pagatore, child.utenza, child.id_tipologia));
            bus.post(new LoadPresenzeEvent(child.utenza, child.id_tipologia, ApiService.presenzeMonth));
            bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));
            bus.post(new LoadNewsEvent(child.id_commessa, child.utenza));
            bus.post(new LoadContattiEvent(child.id_commessa));
            if (showMenu)
                bus.post(new LoadMenuEvent(child.id_commessa, child.utenza, ApiService.dataMenu));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        bus.unregister(this);
        progressDialog.dismiss();
    }

    @Subscribe
    public void onChildLoadedEvent(ChildLoadedEvent event) {
        progressDialog.dismiss();
        detailsLoaded = true;
        childDetails = event.figlioDettagli;

        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(getFragmentTag(R.id.viewpager, 0));
        if (frg != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }

    @Subscribe
    public void onEstrattoContoLoadedEvent(EstrattoContoLoadedEvent event) {
        progressDialog.dismiss();
        detailsLoaded = true;

        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(getFragmentTag(R.id.viewpager, 2));
        if (frg != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }

    @Subscribe
    public void onPresenzeLoadedEvent(PresenzeLoadedEvent evt) {
        progressDialog.dismiss();
        detailsLoaded = true;

    }

    @Subscribe
    public void onNewsLoadedEvent(NewsLoadedEvent evt) {
        progressDialog.dismiss();
        detailsLoaded = true;

        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(getFragmentTag(R.id.viewpager, (showMenu ? 4 : 3)));
        if (frg != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }

    @Subscribe
    public void onContattiLoadedEvent(ContattiLoadedEvent evt) {
        progressDialog.dismiss();
        detailsLoaded = true;

        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(getFragmentTag(R.id.viewpager, (showMenu ? 5 : 4)));
        if (frg != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }

    @Subscribe
    public void onConfigLoadedEvent(ConfigLoadedEvent evt) {
        progressDialog.dismiss();
        detailsLoaded = true;

        Log.e("CONFIG", "tot:" + ApiService.configCommessa.size());
        ArrayList<Configurazione> confCommessa = ApiService.configCommessa;
        for (int i = 0; i < confCommessa.size(); i++) {
            Configurazione conf = confCommessa.get(i);
            Log.e("CONFIG", conf.codice + ":" + conf.valore);
            if (conf.codice.equals("ABILITA_MENU") && conf.valore.equals("0")) {
                showMenu = false;
                nrTab = 5;
                adapter.notifyDataSetChanged();
             //vers 1.7
            } else  if(conf.codice.equals("ABILITA_PAGAMENTI") &&  conf.valore.equals("1")){
                   canPay= true;
            }else  if(conf.codice.equals("URL_PAGAMENTI") &&  !conf.valore.equals("")){
                urlPagamenti= conf.valore;
            }


        }
    }


    @Subscribe
    public void onMenuLoadedEvent(MenuLoadedEvent evt) {
        progressDialog.dismiss();
        detailsLoaded = true;

        Log.e("MENU", "tot:" + ApiService.menu.size());
        Fragment frg = null;
        frg = getSupportFragmentManager().findFragmentByTag(getFragmentTag(R.id.viewpager, 3));
        if (frg != null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.child_details_menu, menu);

        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.menu_mod_ord_desc).setChecked(getDecrescente());
        menu.findItem(R.id.menu_effettua_ricarica).setVisible(canPay);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            new AsyncPhotoSave().execute();
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                Cursor c = getContentResolver().query(uri, null, null, null, null);
                c.moveToNext();
                // String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
                Path(requestCode, resultCode, data);
                c.close();

                bitmap = ChildDetailActivity.modifyOrientation(bitmap, strImagePath);
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.3), (int) (bitmap.getHeight() * 0.3), true);

                saveImage(resized, filename.toString());
                saveImage(getThumbnail(Uri.fromFile(filename)), filename_icon.toString());

                loadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // boolean result = Utility.checkPermission(getApplicationContext());
        // Log.e("PERMESSO",  ""+result);
        Log.e("MENU", "" + item.getItemId());

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, ChildListActivity.class));
                return true;
            case R.id.take_photo_menu_btn:
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (filename != null && filename_icon != null) {
                    Log.e("FOTO", "QUASI::" + imageUri);



                        try {



                            boolean result = checkPermission();
                            if (result) {

                                readExternalStorage();
                                /*Intent intent = new Intent();
                                // Show only images, no videos or anything else
                                intent.setType("image*//**//*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                // Always show the chooser (if there are multiple options available)
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);*/
                                }


                        } catch (Exception e) {
                            Log.e("FOTO", "ex:" + e.getMessage());
                        }

                }
                return true;
            case R.id.menu_mod_data_inizio:
                DatePickerFragment dpfDa = new DatePickerFragment().newInstance();

                Bundle bdDa = new Bundle();
                bdDa.putString("data", data_da);
                dpfDa.setArguments(bdDa);
                dpfDa.setCallBack(onDateDataDaSet);

                dpfDa.show(getFragmentManager().beginTransaction(), "DatePickerFragment");

                return true;
            case R.id.menu_mod_data_fine:
                DatePickerFragment dpfA = new DatePickerFragment().newInstance();

                Bundle bdA = new Bundle();
                bdA.putString("data", data_a);

                dpfA.setArguments(bdA);
                dpfA.setCallBack(onDateDataASet);
                dpfA.show(getFragmentManager().beginTransaction(), "DatePickerFragment");

                return true;
            case R.id.menu_mostra_presenze:
                tipo_estrazione = "0";
                bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));

                return true;
            case R.id.menu_mostra_ricariche:
                tipo_estrazione = "2";
                bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));

                return true;
            case R.id.menu_mostra_tutto:
                tipo_estrazione = "1";
                bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));

                return true;
            case R.id.menu_mod_ord_desc:
                if(item.isChecked()){
                    item.setChecked(false);
                    setDecrescente(false);
                }else{
                    item.setChecked(true);
                    setDecrescente(true);
                }
                bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));

                return true;
            case R.id.menu_effettua_ricarica:
                //if abilitato a ricarica
                if(isOnline()) {
                    openWebPage("MeetsFood", 106107, 1);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openWebPage(String user, int utenza, int tipologia) {
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.detail_tabs);

        String url = urlPagamenti;
        url = url + "?user="+ user ;
        url = url + "&utenza=" + utenza;
        url = url + "&id_tipologia=" + tipologia;
        webButton = (Button) findViewById(R.id.btn_pagamenti_close);
        webTitle = (TextView) findViewById(R.id.title_web_view);
        webView = (WebView) findViewById(R.id.webViewMeetsFoodPay);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setUserAgentString("Android");
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWindow().setTitle(title); //Set Activity tile to page title.
            }
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d("LogTag", message);
                if(message.equals("payment_succes")){
                    webView.setVisibility(View.GONE);
                    webButton.setVisibility(View.GONE);
                    webTitle.setVisibility(View.GONE);
                }else if(message.equals("payment_failure")){
                    webView.setVisibility(View.GONE);
                    webButton.setVisibility(View.GONE);
                    webTitle.setVisibility(View.GONE);
                }

                result.confirm();
                return true;
            }
        });
        webView.loadUrl(url);
        //webView.loadUrl("javascript:alert(paymentOk())");
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.endsWith("#closeWebview")){
                   webView.setVisibility(View.GONE);
                    webButton.setVisibility(View.GONE);
                    webTitle.setVisibility(View.GONE);
                    tipo_estrazione = "2";
                    bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));
                    tabLayout.getTabAt(2).select();
                }
            }
        });

        webButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //webView.reload();
               // webView.destroy();
                webView.setVisibility(View.GONE);
                webButton.setVisibility(View.GONE);
                webTitle.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                tipo_estrazione = "2";
                bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));
                tabLayout.getTabAt(2).select();
            }
        });
        webView.setVisibility(View.VISIBLE);
        webButton.setVisibility(View.VISIBLE);
        webTitle.setVisibility(View.VISIBLE);
       // setRequestedOrientation(this.getResources().getConfiguration().orientation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }



    private void readExternalStorage() {

        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;


    @TargetApi(19)
    //@Override

    protected void Path(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getData() != null && resultCode == RESULT_OK) {

            boolean isImageFromGoogleDrive = false;
            Uri uri = data.getData();

            if (isKitKat && DocumentsContract.isDocumentUri(getApplicationContext(), uri)) {

                if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        strImagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        Pattern DIR_SEPORATOR = Pattern.compile("/");
                        Set<String> rv = new HashSet<>();
                        String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
                        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
                        String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");
                        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
                            if (TextUtils.isEmpty(rawExternalStorage)) {
                                rv.add("/storage/sdcard0");
                            } else {
                                rv.add(rawExternalStorage);
                            }
                        } else {
                            String rawUserId;
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                rawUserId = "";
                            } else {
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                                String[] folders = DIR_SEPORATOR.split(path);
                                String lastFolder = folders[folders.length - 1];
                                boolean isDigit = false;
                                try {
                                    Integer.valueOf(lastFolder);
                                    isDigit = true;
                                } catch (NumberFormatException ignored) {
                                }
                                rawUserId = isDigit ? lastFolder : "";
                            }
                            if (TextUtils.isEmpty(rawUserId)) {
                                rv.add(rawEmulatedStorageTarget);
                            } else {
                                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
                            }
                        }
                        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                            String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                            Collections.addAll(rv, rawSecondaryStorages);
                        }
                        String[] temp = rv.toArray(new String[rv.size()]);

                        for (int i = 0; i < temp.length; i++) {
                            File tempf = new File(temp[i] + "/" + split[1]);
                            if (tempf.exists()) {
                                strImagePath = temp[i] + "/" + split[1];
                            }
                        }
                    }
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    String id = DocumentsContract.getDocumentId(uri);
                    Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {
                            column
                    };

                    try {
                        cursor = getContentResolver().query(contentUri, projection, null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            final int column_index = cursor.getColumnIndexOrThrow(column);
                            strImagePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String docId = DocumentsContract.getDocumentId(uri);
                    String[] split = docId.split(":");
                    String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{
                            split[1]
                    };

                    Cursor cursor = null;
                    String column = "_data";
                    String[] projection = {
                            column
                    };

                    try {
                        cursor = getContentResolver().query(contentUri, projection, selection, selectionArgs,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(column);
                            strImagePath = cursor.getString(column_index);
                        }
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                } else if ("com.google.android.apps.docs.storage".equals(uri.getAuthority())) {
                    isImageFromGoogleDrive = true;
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                String column = "_data";
                String[] projection = {
                        column
                };

                try {
                    cursor = getContentResolver().query(uri, projection, null, null,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        int column_index = cursor.getColumnIndexOrThrow(column);
                        strImagePath = cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                strImagePath = uri.getPath();
            }


            if (isImageFromGoogleDrive) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    //iv.setImageBitmap(bitmap);
                    //tvImagePath.setText(getResources().getString(R.string.str_image_google_drive));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                File f = new File(strImagePath);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
                //iv.setImageBitmap(bitmap);
                //tvImagePath.setText(getResources().getString(R.string.str_image_path) + ": " + strImagePath);
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
//---------------------------------------
// Checkpermission


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission()
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write calendar permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readExternalStorage();
                } else {
//code for deny
                }
                break;
        }
    }

   //--------------------------------------

//datepicker listener


    DatePickerDialog.OnDateSetListener onDateDataDaSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            progressDialog.show();

            Calendar cal = Calendar.getInstance();
            cal.set(year, monthOfYear, dayOfMonth);

            data_da = sdf.format(cal.getTime());
            setDaData(data_da);
            bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));
        }
    };
    DatePickerDialog.OnDateSetListener onDateDataASet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            progressDialog.show();

            Calendar cal = Calendar.getInstance();
            cal.set(year, monthOfYear, dayOfMonth);

            data_a = sdf.format(cal.getTime());
            bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));
        }
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ChildDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class SectionPagerAdapter extends FragmentPagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (showMenu) {
                switch (position) {
                    case 0:
                        InfoFragment f = InfoFragment.newInstance();
                        return f;
                    case 1:
                        return new PresenzeFragment();
                    case 2:
                        return new ContoFragment();
                    case 3:
                        return new MenuFragment();
                    case 4:
                        return new NewsFragment();
                    case 5:
                        return new ContattiFragment();
                    default:
                        return new InfoFragment();
                }
            } else {
                switch (position) {
                    case 0:
                        InfoFragment f = InfoFragment.newInstance();
                        return f;
                    case 1:
                        return new PresenzeFragment();
                    case 2:
                        return new ContoFragment();
                    case 3:
                        return new NewsFragment();
                    case 4:
                        return new ContattiFragment();
                    default:
                        return new InfoFragment();
                }
            }
        }

        @Override
        public int getCount() {
            return nrTab;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (showMenu) {
                switch (position) {
                    case 0:
                        return "Info";
                    case 1:
                        return "Presenze";
                    case 2:
                        return "Estratto conto";
                    case 3:
                        return "Menu";
                    case 4:
                        return "News";
                    case 5:
                        return "Contatti";
                    default:
                        return "Info";
                }
            } else {
                switch (position) {
                    case 0:
                        return "Info";
                    case 1:
                        return "Presenze";
                    case 2:
                        return "Estratto conto";
                    case 3:
                        return "News";
                    case 4:
                        return "Contatti";
                    default:
                        return "Info"+position;
                }
            }
        }
    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition) {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    private void saveImage(Bitmap bitmap, String path) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            File f = new File(path);
            FileOutputStream fo = openFileOutput(f.getName(), Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError o) {
            o.printStackTrace();
        }
    }

    private Bitmap getThumbnail(Uri uri) {
        File image = new File(uri.getPath());

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        bounds.inScaled = false;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 100;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    public class AsyncPhotoSave extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.show();
                    }
                });
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                int degree = ChildDetailActivity.getCameraPhotoOrientation(imageUri, imageUri.getPath());
                bitmap = ChildDetailActivity.rotate(bitmap, degree);
                // bitmap = modifyOrientation(bitmap, imageUri.getPath());
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.5), (int) (bitmap.getHeight() * 0.5), true);

                saveImage(resized, filename.toString());
                saveImage(getThumbnail(Uri.fromFile(filename)), filename_icon.toString());
                if (filename_tmp.exists())
                    filename_tmp.delete();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadImage();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            return null;
        }
    }

    private void loadImage() {
        ImageView image = (ImageView) findViewById(R.id.header_image);
        image.setImageDrawable(null);
        image.invalidate();
        Picasso.with(this).load(filename).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerCrop().into(image);
    }


    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        progressDialog.dismiss();
    }

    @Subscribe
    public void onReloadInterface(ReloadInterfaceEvent evt) {
        progressDialog.show();

        if (data_da == null || data_a == null) {
            Calendar cal = Calendar.getInstance();
            data_a = sdf.format(cal.getTime());

            getDaData();
        }


        bus.post(new LoadChildEvent(child.pagatore, child.utenza, child.id_tipologia));
        bus.post(new LoadPresenzeEvent(child.utenza, child.id_tipologia, ApiService.presenzeMonth));
        bus.post(new LoadEstrattoContoEvent(child.pagatore, child.utenza, data_da, data_a, child.id_tipologia, tipo_estrazione));
        bus.post(new LoadNewsEvent(child.id_commessa, child.utenza));
        bus.post(new LoadContattiEvent(child.id_commessa));
        if (showMenu)
            bus.post(new LoadMenuEvent(child.id_commessa, child.utenza, ApiService.dataMenu));
    }

    public void reloadPresenzeEvent(Date d) {
        ApiService.presenzeMonth = d;
        bus.post(new LoadPresenzeEvent(child.utenza, child.id_tipologia, d));
    }

    public void reloadMenuEvent(Date d) {
        ApiService.dataMenu = d;
        bus.post(new LoadMenuEvent(child.id_commessa, child.utenza, ApiService.dataMenu));
    }

    public static int getCameraPhotoOrientation(Uri imageUri, String imagePath) {
        Log.e("getCamera", "SONO QUIIIII");
        int rotate = 0;
        try {
            //getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        Log.e("modifyOrientation", "modifyOrientation > START");
        ExifInterface ei = new ExifInterface(image_absolute_path);
        Log.e("modifyOrientation", image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        Log.e("modifyOrientation", orientation + "");
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Context getContext(){

        return context;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    public void setDaData(String daDate){

        SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
        editor.putString("da_data", daDate);
        editor.apply();
    }
    public void setDecrescente(boolean decrescente){

        SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();
        editor.putBoolean("decrescente", decrescente);
        editor.apply();
        getDecrescente();
    }

    public void getDaData(){
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String daData = prefs.getString("da_data", null);
        if (daData != null) {
            data_da = daData;
        }else{
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -12);
            data_da = sdf.format(cal.getTime());
        }
    }
    public boolean getDecrescente(){
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
         decrescente = prefs.getBoolean("decrescente", false);

        return decrescente;
    }

    private boolean isOnline() {
        return ConnectivityUtils.isNetworkConnected(context);
    }

}
