package com.sidera.meetsfood;


import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.ContabilitaRowV20;
import com.sidera.meetsfood.api.beans.Disdette;
import com.sidera.meetsfood.api.beans.Tariffe;
import com.sidera.meetsfood.events.ApiErrorEvent;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.DisdettaEvent;
import com.sidera.meetsfood.events.DisdettaInfoLoadedEvent;
import com.sidera.meetsfood.events.DisdettaSettedEvent;
import com.sidera.meetsfood.events.LoadDisdettaInfoEvent;
import com.sidera.meetsfood.events.LoadTariffeEvent;
import com.sidera.meetsfood.events.PastoInBiancoEvent;
import com.sidera.meetsfood.events.PastoInBiancoSettedEvent;
import com.sidera.meetsfood.events.PresenzeLoadedEvent;
import com.sidera.meetsfood.events.TariffeLoadedEvent;
import com.sidera.meetsfood.view.materialcalendarview.CalendarDay;
import com.sidera.meetsfood.view.materialcalendarview.DayViewDecorator;
import com.sidera.meetsfood.view.materialcalendarview.DayViewFacade;
import com.sidera.meetsfood.view.materialcalendarview.MaterialCalendarView;
import com.sidera.meetsfood.view.materialcalendarview.OnDateChangedListener;
import com.sidera.meetsfood.view.materialcalendarview.OnMonthChangedListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.text.Html.fromHtml;
import static com.sidera.R.drawable.today;


public class PresenzeFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Integer> presenze = new ArrayList<Integer>();
    private List<Integer> assenze = new ArrayList<Integer>();
    private List<Integer> inBianco = new ArrayList<Integer>();
    private List<Integer> disdette = new ArrayList<Integer>();
    private List<Integer> daCalendario = new ArrayList<Integer>();
    private int actualMonth;

    ProgressDialog progressDialog;

    private CalendarDay dayToSet;
    public static final int DIALOG_FRAGMENT = 1;
    private MaterialCalendarView calendarView;
    private Bus bus;
    Context context;
    public CalendarDay selectedDate;
    public MaterialCalendarView selectView;
    //Circle Style set
    public int disdetteStyle = 0;
    public int calendarStyle = 0;
    public int pastoStyle = 0;
    public Date convertedDate;

    public static PresenzeFragment newInstance() {
        PresenzeFragment fragment = new PresenzeFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void getDaysInfo(){
        presenze = new ArrayList<Integer>();
        assenze = new ArrayList<Integer>();
        inBianco = new ArrayList<Integer>();
        disdette = new ArrayList<Integer>();
        daCalendario = new ArrayList<Integer>();

        actualMonth = CalendarDay.from(ApiService.presenzeMonth).getMonth();

        ArrayList<ContabilitaRowV20> presenze = ApiService.presenze;
        if (presenze != null) {
            Log.e("PRESENZE", String.valueOf(presenze.size()));
            for(ContabilitaRowV20 r: presenze) {
                Calendar c = Calendar.getInstance();
                c.setTime(r.data);
                if (r.pasto_bianco.equals("0")) {
                    if ( r.flag_giorno.equals("P")) {
                        this.presenze.add(c.get(Calendar.DATE));
                    } else if ( r.flag_giorno.equals("DIS")) {
                         this.disdette.add(c.get(Calendar.DATE));
                    } else if ( r.flag_giorno.equals("CAL")) {
                        this.daCalendario.add(c.get(Calendar.DATE));
                    }else if ( r.flag_giorno.equals("BIANCO")) {
                        this.inBianco.add(c.get(Calendar.DATE));
                    }
                }else {
                    //this.inBianco.add(c.get(Calendar.DATE));
                }
            }
        }
    }

    public PresenzeFragment() {
        getDaysInfo();
        bus = BusProvider.getInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ChildDetailActivity.getContext();

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading ....");


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getDaysInfo();
    }

    @Override
    public void onResume() {
        super.onResume();

        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        bus.unregister(this);
    }


    @Subscribe
    public void onTariffeLoadedEvent(TariffeLoadedEvent evt) {
        progressDialog.dismiss();

        onTariffeRecived(selectedDate,selectView);
        //detailsLoaded = true;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void onTariffeRecived(final CalendarDay selectedDate, final MaterialCalendarView selectView) {

        if(daCalendario.contains(selectedDate.getDay())) {

            final Date daCalendarioDate = selectedDate.getDate();
            //bus.post(new LoadTariffeEvent( ApiService.figlioTestata.id_commessa, ApiService.figlioTestata.utenza, daCalendarioDate));
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            String TitleData = sdf.format(daCalendarioDate);
            List<String> listItems = new ArrayList<String>();
            listItems.add("Richiedi: PASTO IN BIANCO ");

            ArrayList<Tariffe> tariffe = ApiService.tariffe;

            if ((tariffe) == null) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("")
                        .setMessage("Errore")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            } else {

                final  ArrayList<String> DisdettaOptions = new ArrayList<String>();
                DisdettaOptions.add("");
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.PauseDialog);
                builder.setCancelable(false);
                //builder.set();
                TextView textView = new TextView(context);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                //textView.setHeight();
                textView.setGravity(Gravity.CENTER);
                //textView.setBackgroundColor(Color.GREEN);
                textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                    textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Scegli cosa vuoi fare:</div>", Html.FROM_HTML_MODE_COMPACT)));
                } else {
                    textView.setText(noTrailingwhiteLines(fromHtml("<h4>"+TitleData+"</h4><div>Scegli cosa vuoi fare:</div>")));
                }
                builder.setCustomTitle(textView);

                if (tariffe != null) {
                    for (Tariffe t : tariffe) {
                        DisdettaOptions.add(t.codice);
                        listItems.add("Disdici: "+t.info);

                    }
                }
                final CharSequence[] charSequenceItems = listItems.toArray(new CharSequence[listItems.size()]);
                builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            String responce = "pasto in bianco";
                            bus.post(new PastoInBiancoEvent( ApiService.figlioTestata.id_commessa, ApiService.figlioTestata.utenza, daCalendarioDate,DisdettaOptions.get(1)));
                            ChildDetailActivity act = (ChildDetailActivity) getActivity();
                            act.reloadPresenzeEvent(selectedDate.getDate());
                            act.progressDialog.show();

                            if (selectedDate.getMonth() == CalendarDay.today().getMonth() && presenze.size() == 0) {
                                getDaysInfo();
                            } else {
                                presenze.clear();
                                assenze.clear();
                                inBianco.clear();
                                disdette.clear();
                                daCalendario.clear();
                            }
                            selectView.invalidateDecorators();
                        }else{
                            bus.post(new DisdettaEvent( ApiService.figlioTestata.id_commessa, ApiService.figlioTestata.utenza, daCalendarioDate,DisdettaOptions.get(which)));

                           // ArrayList<Disdette> disdettaPasto = ApiService.disdetta;
                            //String InfoPastInB = disdettaPasto.info;

                            ChildDetailActivity act = (ChildDetailActivity) getActivity();
                            act.reloadPresenzeEvent(selectedDate.getDate());
                            act.progressDialog.show();

                            if (selectedDate.getMonth() == CalendarDay.today().getMonth() && presenze.size() == 0) {
                               getDaysInfo();
                            } else {
                                presenze.clear();
                                assenze.clear();
                                inBianco.clear();
                                disdette.clear();
                                daCalendario.clear();
                            }
                            selectView.invalidateDecorators();

                        }

                    }
                });

                builder.setNegativeButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the user clicked on Cancel
                    }
                });

                final AlertDialog dialog = builder.create();
                dialog.show(); //show() should be called before dialog.getButton().


                final Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams negativeButtonLL = (LinearLayout.LayoutParams) negativeButton.getLayoutParams();
                negativeButtonLL.gravity = Gravity.CENTER;
                negativeButton.setLayoutParams(negativeButtonLL);
            }

        }
    }
    //ArrayList<Disdette> setPastoInBianco = ApiService.pastoInBianco;
   // String InfoPastInB = setPastoInBianco.info;

    @Subscribe
    public void onDisdettaSettedEvent(DisdettaSettedEvent evt) {
        progressDialog.dismiss();
        onDisdettaRecived(selectedDate);

        //detailsLoaded = true;

    }

    private void onDisdettaRecived(CalendarDay selectedDate) {



            final Date disdettaDate = selectedDate.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            String TitleData = sdf.format(disdettaDate);



            Disdette disdettaSetted = ApiService.disdetta;

            if (disdettaSetted.response.contains("SQLEXCEPTION")) {

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }

                    TextView textView = new TextView(context);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(20);
                    textView.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    textView.setLayoutParams(lp);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                        textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Disdetta Error:</div>", Html.FROM_HTML_MODE_COMPACT)));
                    } else {
                        textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Disdetta Error:</div>")));
                    }

                //Selezione esito negativo

                    String tipoErrore = disdettaSetted.response;
                    tipoErrore = tipoErrore.replaceAll("SQLEXCEPTION ","");
                    String descEsito = "";
                if(tipoErrore.trim().equals("DATA_MIN")){
                    descEsito = getString(R.string.msg_data_min);
                }else if(tipoErrore.trim().equals("DATA_MAX")){
                    descEsito = getString(R.string.msg_data_max);
                }else if(tipoErrore.trim().equals("DATA_OUT")){
                    descEsito = getString(R.string.msg_data_out);
                }else if(tipoErrore.trim().equals("DISDETTA_DOPPIA")){
                    descEsito = getString(R.string.msg_disdetta_doppia);
                }

                    builder.setCustomTitle(textView)
                            .setMessage(descEsito)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

            } else{
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
                } else {
                    builder = new AlertDialog.Builder(context);
                }

                TextView textView = new TextView(context);
                textView.setTextColor(Color.BLACK);
                textView.setTextSize(20);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 0, 0);
                textView.setLayoutParams(lp);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                    textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Esito Disdetta:</div>", Html.FROM_HTML_MODE_COMPACT)));
                } else {
                    textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Esito Disdetta:</div>")));
                }

                String descEsito = "";
                descEsito = getString(R.string.msg_disdetta_ok);

                    builder.setCustomTitle(textView)
                        .setMessage(descEsito)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
    }

    @Subscribe
    public void onPastoInBiancoSettedEvent(PastoInBiancoSettedEvent evt) {
        progressDialog.dismiss();
        onPastoInBiancoRecived(selectedDate);

        //detailsLoaded = true;

    }

    private void onPastoInBiancoRecived(CalendarDay selectedDate) {



        final Date disdettaDate = selectedDate.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        String TitleData = sdf.format(disdettaDate);



        Disdette pastoinBiancoSetted = ApiService.pastoInBianco;

        if (pastoinBiancoSetted.response.contains("SQLEXCEPTION")) {

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
            } else {
                builder = new AlertDialog.Builder(context);
            }

            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(20);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 0);
            textView.setLayoutParams(lp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Cambio pasto error:</div>", Html.FROM_HTML_MODE_COMPACT)));
            } else {
                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Cambio pasto error:</div>")));
            }

            //Selezione esito negativo

            String tipoErrore = pastoinBiancoSetted.response;
            tipoErrore = tipoErrore.replaceAll("SQLEXCEPTION ","");
            String descEsito = "";
            if(tipoErrore.trim().equals("DATA_MIN")){
                descEsito = getString(R.string.msg_data_min);
            }else if(tipoErrore.trim().equals("DATA_MAX")){
                descEsito = getString(R.string.msg_data_max);
            }else if(tipoErrore.trim().equals("DATA_OUT")){
                descEsito = getString(R.string.msg_data_out);
            }else if(tipoErrore.trim().equals("DISDETTA_DOPPIA")){
                descEsito = getString(R.string.msg_disdetta_doppia);
            }

            builder.setCustomTitle(textView)
                    .setMessage(descEsito)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else{
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
            } else {
                builder = new AlertDialog.Builder(context);
            }

            TextView textView = new TextView(context);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(20);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 0);
            textView.setLayoutParams(lp);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Esito cambio pasto:</div>", Html.FROM_HTML_MODE_COMPACT)));
            } else {
                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Esito cambio pasto:</div>")));
            }

            String descEsito = "";
            descEsito = getString(R.string.msg_pastoinbianco_ok);

            builder.setCustomTitle(textView)
                    .setMessage(descEsito)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }



    }


    @Subscribe
    public void onDisdettaInfoLoadedEvent(DisdettaInfoLoadedEvent evt) {
         progressDialog.dismiss();
        onInfoRecived(selectedDate);

        //detailsLoaded = true;

    }

    private void onInfoRecived(CalendarDay selectedDate) {

        //Toast.makeText(, date, Toast.LENGTH_LONG).show();

        if(disdette.contains(selectedDate.getDay()) || inBianco.contains(selectedDate.getDay()) ) {

            final Date disdettaDate = selectedDate.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            SimpleDateFormat sdfInfo = new SimpleDateFormat("dd MMMM yyyy HH:mm");
            String TitleData = sdf.format(disdettaDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            ArrayList<Disdette> disdetta = ApiService.disdettaInfo;

            if (!disdetta.isEmpty()) {

                        if(disdetta.get(0).tipologia.equals("pastobianco") ){

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
                            } else {
                                builder = new AlertDialog.Builder(context);
                            }

                            TextView textView = new TextView(context);
                            textView.setTextColor(Color.BLACK);
                            textView.setTextSize(20);
                            textView.setGravity(Gravity.CENTER);
                            //textView.setBackgroundColor(Color.GREEN);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 0, 0);
                            textView.setLayoutParams(lp);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Situazione del giorno:</div>", Html.FROM_HTML_MODE_COMPACT)));
                            } else {
                                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Situazione del giorno:</div>")));
                            }

                            try {
                                 convertedDate = dateFormat.parse(disdetta.get(0).dateInserted);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            CharSequence messageDis = "";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                                messageDis = noTrailingwhiteLines(fromHtml("E' stato richiesto un <b> pasto in bianco </b> in data " + sdfInfo.format(convertedDate) , Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                messageDis =  noTrailingwhiteLines(fromHtml("E' stato richiesto un <b> pasto in bianco </b> in data " + sdfInfo.format(convertedDate)));
                            }
                            builder.setCustomTitle(textView)
                                    .setMessage(messageDis)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }else{
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
                            } else {
                                builder = new AlertDialog.Builder(context);
                            }

                            try {
                                convertedDate = dateFormat.parse(disdetta.get(0).dateInserted);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            CharSequence messageDis = "";
                            if(disdetta.get(0).revoked){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                                    messageDis =  noTrailingwhiteLines(fromHtml("Revocato:  <b>"+ disdetta.get(0).info + "</b>  <br> in data "  + sdfInfo.format(convertedDate) , Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    messageDis =  noTrailingwhiteLines(fromHtml("Revocato:  <b>"+ disdetta.get(0).info + "</b>  <br> in data "  + sdfInfo.format(convertedDate)));
                                }
                            }else{
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                                    messageDis = noTrailingwhiteLines(fromHtml("Disdetto:  <b>"+ disdetta.get(0).info + "</b>  <br> in data "  + sdfInfo.format(convertedDate) , Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    messageDis = noTrailingwhiteLines(fromHtml("Disdetto:  <b>"+ disdetta.get(0).info + "</b>  <br> in data "  + sdfInfo.format(convertedDate)));
                                }
                            }

                            TextView textView = new TextView(context);
                            textView.setTextColor(Color.BLACK);
                            textView.setTextSize(20);
                            textView.setGravity(Gravity.CENTER);
                            //textView.setBackgroundColor(Color.GREEN);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(0, 0, 0, 0);
                            textView.setLayoutParams(lp);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Situazione del giorno:</div>", Html.FROM_HTML_MODE_COMPACT)));
                            } else {
                                textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4><div>Situazione del giorno:</div>")));
                            }
                            builder.setCustomTitle(textView)
                                    .setMessage(messageDis)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
            } else{
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("")
                        .setMessage("Errore")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        }
    }


    @Subscribe
    public void onApiErrorEvent(ApiErrorEvent evt) {
        progressDialog.dismiss();
       String error =  evt.getErrorMessage();

        error.isEmpty();

        //detailsLoaded = true;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDaysInfo();
        View rootView = inflater.inflate(R.layout.fragment_presenze, container, false);
        final AppCompatActivity act = (AppCompatActivity)getActivity();
        calendarView = (MaterialCalendarView)rootView.findViewById(R.id.calendarView);
        configCalendar(calendarView);
      //  date = calendarView.getSelectedDate().getDate();
        return rootView;
    }

    private void configCalendar(final MaterialCalendarView calendarView){
       /* calendarView.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date) {
                if(date != null) {
                    dayToSet = date;
                    DialogFragment dialog = new SetDayDialog();
                    Bundle args = new Bundle();
                    args.putString("date", String.format("%d/%d/%d", date.getDay(), date.getMonth() + 1, date.getYear()));
                    if(presenze.contains(date.getDay()))
                        args.putInt("presenza", 0);
                    else if(assenze.contains(date.getDay()))
                        args.putInt("presenza", 1);
                    else if(inBianco.contains(date.getDay()))
                        args.putInt("presenza", 2);
                    else
                        return;

                    dialog.setArguments(args);
                    dialog.setTargetFragment(PresenzeFragment.this, DIALOG_FRAGMENT);
                    dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
                }
            }
        });*/

        calendarView.setSelectionColor(ContextCompat.getColor(getContext(), R.color.background));
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                ChildDetailActivity act = (ChildDetailActivity) getActivity();
                act.reloadPresenzeEvent(date.getDate());
                act.progressDialog.show();

                if (date.getMonth() == CalendarDay.today().getMonth() && presenze.size() == 0) {
                    getDaysInfo();
                } else {
                    presenze.clear();
                    assenze.clear();
                    inBianco.clear();
                    disdette.clear();
                    daCalendario.clear();

                }
                widget.invalidateDecorators();
            }
        });




        calendarView.setOnDateChangedListener(new OnDateChangedListener(){
            @Override
            public void onDateChanged(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date) {

                selectedDate = date;
                selectView = widget;

                if(presenze.contains(date.getDay())) {
                    final Date disdettaDate = selectedDate.getDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                    String TitleData = sdf.format(disdettaDate);

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context  , R.style.PauseDialog);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    TextView textView = new TextView(context);
                    textView.setTextColor(Color.BLACK);
                    textView.setTextSize(20);
                    textView.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 0, 0);
                    textView.setLayoutParams(lp);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//nougat
                        textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4>", Html.FROM_HTML_MODE_COMPACT)));
                    } else {
                        textView.setText(noTrailingwhiteLines(fromHtml("<h4 align='center'>"+TitleData+"</h4>")));
                    }
                    builder.setCustomTitle(textView)
                            .setMessage("Hai effettuato la consumazione.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }else if(inBianco.contains(date.getDay())) {
                     bus.post(new LoadDisdettaInfoEvent( ApiService.figlioTestata.id_commessa, ApiService.figlioTestata.utenza, date.getDate()));
                     progressDialog.show();
                }else if(disdette.contains(date.getDay())) {
                     bus.post(new LoadDisdettaInfoEvent( ApiService.figlioTestata.id_commessa, ApiService.figlioTestata.utenza, date.getDate()));
                     progressDialog.show();
                }else if(daCalendario.contains(date.getDay())) {
                     bus.post(new LoadTariffeEvent( ApiService.figlioTestata.id_commessa, ApiService.figlioTestata.utenza, date.getDate()));
                     progressDialog.show();
                }


             }

        });

        calendarView.setFirstDayOfWeek(2);
        DayViewDecorator decoratorToday = new DayViewDecorator() {
            int today;
            int bgColor =  ContextCompat.getColor(getContext(), R.color.ColorPrimaryDark);
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(CalendarDay.today().equals(day)) {
                    today = day.getDay();
                    return true;
                }
                return false;
            }



            @Override
            public void decorate(DayViewFacade view) {
                Drawable drawable;
                drawable = ContextCompat.getDrawable(context,     R.drawable.today);
                if(daCalendario.contains(CalendarDay.today().getDay()))
                   // bgColor = ContextCompat.getColor(getContext(),R.color.ColorPrimary);
                    drawable = ContextCompat.getDrawable(context,     R.drawable.today_da_calendario);
                else if(presenze.contains(CalendarDay.today().getDay()))
                    drawable = ContextCompat.getDrawable(context,     R.drawable.today_pasto_effettuato);
                else if(disdette.contains(CalendarDay.today().getDay()))
                    drawable = ContextCompat.getDrawable(context,     R.drawable.today_disdetta);
                else if(inBianco.contains(CalendarDay.today().getDay()))
                    drawable = ContextCompat.getDrawable(context,     R.drawable.today_pasto_in_bianco);
                ShapeDrawable shape = new ShapeDrawable(new OvalShape());
                shape.setShaderFactory(new ShapeDrawable.ShaderFactory() {
                    @Override
                    public Shader resize(int width, int height) {
                        return new LinearGradient( 0, 0, 0, 0, bgColor, R.color.bg_white, Shader.TileMode.REPEAT);

                    }
                });


                view.setSelectionDrawable(drawable);
                view.addSpan(new ForegroundColorSpan(Color.BLACK));

            }
        };
        DayViewDecorator decoratorInBianco = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(day.equals(CalendarDay.today())){
                    //not change
                    return false;
                }else if(inBianco.contains(day.getDay()))
                    return true;
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setSelectionDrawable(ContextCompat.getDrawable(context,     R.drawable.pasto_in_bianco));


            }
        };
        DayViewDecorator decoratorPresenze = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(day.equals(CalendarDay.today())){
                    //not change
                    return false;
                }else if(presenze.contains(day.getDay()))
                    return true;
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
              // view.addSpan(new RoundedBackgroundSpan(35, ContextCompat.getColor(getContext(), R.color.green), ContextCompat.getColor(getContext(), R.color.white)));
                // view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.green)));
                view.setSelectionDrawable(ContextCompat.getDrawable(context,     R.drawable.pasto_effettuato));
            }
        };
        DayViewDecorator decoratorAssenze = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(day.equals(CalendarDay.today())){
                    //not change
                    return false;
                }else if(assenze.contains(day.getDay()))
                    return true;
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red)));
            }
        };
        DayViewDecorator decoratorDaCalendario = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(day.equals(CalendarDay.today())){
                    //not change
                    return false;
                }else if(daCalendario.contains(day.getDay()))
                    return true;
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
               // view.addSpan(new RoundedBackgroundSpan(35, ContextCompat.getColor(getContext(), R.color.ColorPrimary), ContextCompat.getColor(getContext(), R.color.white)));
                // view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.green)));
                view.setSelectionDrawable(ContextCompat.getDrawable(context,     R.drawable.da_calendario));
            }
        };
        DayViewDecorator decoratorDisdette = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
               if(day.equals(CalendarDay.today())){
                   //not change
                   return false;
               }else if(disdette.contains(day.getDay())){
                   return true;
               }

                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                    view.setSelectionDrawable(ContextCompat.getDrawable(context, R.drawable.disdetta));
            }
        };
        calendarView.addDecorators(decoratorPresenze, decoratorAssenze, decoratorInBianco, decoratorToday,decoratorDaCalendario,decoratorDisdette);
    }

    private CharSequence noTrailingwhiteLines(CharSequence text) {

        while (text.charAt(text.length() - 1) == '\n') {
            text = text.subSequence(0, text.length() - 1);
        }
        return text;
    }

    class RetrieveFeedTask extends AsyncTask<Date,Integer,String> {

        private Exception exception;

        protected String doInBackground(Date... date) {
            String strResponse = "";
            Date data;
            data = date[0];
            try {
                bus.post(new LoadDisdettaInfoEvent( ApiService.figlioTestata.id_commessa, ApiService.figlioTestata.utenza, data));
            } catch (Exception e) {
                strResponse = null;

                Log.e("PIPPO", "EXCEPTION", e);
                e.printStackTrace();
            }
            return strResponse;
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Subscribe
    public void onPresenzeLoadedEvent(PresenzeLoadedEvent evt) {
        Log.e("PRESENZE", "LOADED");
        getDaysInfo();
        calendarView.invalidateDecorators();
    }
}

