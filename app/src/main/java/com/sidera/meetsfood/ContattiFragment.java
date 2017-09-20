package com.sidera.meetsfood;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.sidera.R;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.Configurazione;
import com.sidera.meetsfood.api.beans.Contatti;
import com.sidera.meetsfood.api.beans.News;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class ContattiFragment extends Fragment {
    public ContattiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contatti, container, false);
        LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.card_layout_contatti);
        ll.removeAllViews();
        String link = null;
        Log.i("CONTATTI", "CONTATTI");
        ArrayList<Contatti> contatti = ApiService.contatti != null ? ApiService.contatti : new ArrayList<Contatti>();
        Log.i("CONTATTI", "tot:"+contatti.size());
        ArrayList<Configurazione> confCommessa = ApiService.configCommessa;
        for(Contatti c : contatti){
            Log.i("CONTATTI", c.codice);
            CardView card = (CardView) inflater.inflate(R.layout.contatti_view, container,false);

            if(c.codice.equals("CONTATTO_TELEFONO")) {
                ((TextView) card.findViewById(R.id.content_contatti_text)).setInputType(InputType.TYPE_CLASS_PHONE);
                ((TextView)card.findViewById(R.id.title_contatti_text)).setText(R.string.CONTATTO_TELEFONO);
                ((TextView) card.findViewById(R.id.content_contatti_text)).setAutoLinkMask(Linkify.PHONE_NUMBERS);
                ((TextView)card.findViewById(R.id.content_contatti_text)).setText(c.valore);
                ll.addView(card);
            }if(c.codice.equals("CONTATTO_EMAIL")) {
                ((TextView) card.findViewById(R.id.content_contatti_text)).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                ((TextView)card.findViewById(R.id.title_contatti_text)).setText(R.string.CONTATTO_EMAIL);
                ((TextView) card.findViewById(R.id.content_contatti_text)).setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                ((TextView)card.findViewById(R.id.content_contatti_text)).setText(c.valore);
                ll.addView(card);
            }if(c.codice.equals("CONTATTO_NOME")) {
                ((TextView) card.findViewById(R.id.content_contatti_text)).setInputType(InputType.TYPE_CLASS_TEXT);
                ((TextView)card.findViewById(R.id.title_contatti_text)).setText("Nominativo");
                ((TextView)card.findViewById(R.id.content_contatti_text)).setText(c.valore);
                ll.addView(card);
            }if(c.codice.equals("CONTATTO_SITO")) {
               link = c.valore;
            }









        }

       for (int i = 0; i < confCommessa.size(); i++) {
            Configurazione conf = confCommessa.get(i);
            Log.e("CONFIG", conf.codice + ":" + conf.valore);
           CardView card = (CardView) inflater.inflate(R.layout.contatti_view, container, false);
            if (conf.codice.equals("IMG_COMMESSA") && !conf.valore.equals("")) {


                int SDK_INT = Build.VERSION.SDK_INT;
                if (SDK_INT > 8)
                {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    //your codes here


                Log.e("entrato","nella funzione");

                    DisplayMetrics metrics = new DisplayMetrics();
                    ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    Log.d("larghezza",""+metrics.widthPixels);
                    int larSchermo = metrics.widthPixels;
                    int altSchermo = metrics.heightPixels;
                ImageView iv = new ImageView(getContext());
                URL url = null;
                Bitmap bmp = null;
                try {
                    url = new URL(conf.valore);
                     bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        System.out.println("altezza: "+bmp.getHeight()+"    larghezza: "+bmp.getWidth());
                    if(larSchermo <= 800 || altSchermo <= 800 ){

                        System.out.println("dentro 720");

                           int nuovaAltezza = (bmp.getHeight()*120)/bmp.getWidth();
                        bmp = bmp.createScaledBitmap(bmp, 120, nuovaAltezza, false);


                    }else if(larSchermo <= 1200 || altSchermo <= 1200 ){

                        int nuovaAltezza = (bmp.getHeight()*150)/bmp.getWidth();
                        System.out.println("dentro 1080");
                        bmp = bmp.createScaledBitmap(bmp, 150, nuovaAltezza, false);
                    }






                    //iv.setImageBitmap(bmp);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                    ImageSpan is = new ImageSpan(getContext(), bmp);
                    Drawable d = new BitmapDrawable(getResources(), bmp);
                    ResizeImageSpan iss = new ResizeImageSpan(d ,"  ", 600);
                    SpannableStringBuilder ssb = new SpannableStringBuilder( "  " );
                    ssb.setSpan( iss ,0,1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE );
                    Log.e("arrivato","alla stampa");

                    ((TextView)card.findViewById(R.id.image_contatti_text)).setText( ssb, TextView.BufferType.SPANNABLE );
                    ((TextView) card.findViewById(R.id.link_contatti_text)).setAutoLinkMask(Linkify.WEB_URLS);
                    ((TextView) card.findViewById(R.id.link_contatti_text)).setText(link);
                    //((TextView)card.findViewById(R.id.content_contatti_text)).setText(conf.valore);

                //
                    ll.addView(card);
             }
            }


        }
        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
