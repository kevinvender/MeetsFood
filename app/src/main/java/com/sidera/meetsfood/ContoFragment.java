package com.sidera.meetsfood;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.adapters.ContoListView;
import com.sidera.meetsfood.adapters.EstrattoContoAdapter;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.ContabilitaRowV20;
import com.sidera.meetsfood.api.beans.FiglioTestata;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.content.Context.MODE_PRIVATE;


public class ContoFragment extends Fragment {
    Activity activity;

    public ContoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View rootView;
    EstrattoContoAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_conto, container, false);
        ArrayList<ContabilitaRowV20> l = ApiService.estrattoConto != null ? ApiService.estrattoConto : new ArrayList<ContabilitaRowV20>();

        boolean decrescente;
        decrescente = ChildDetailActivity.decrescente;
        if (decrescente) {
            Collections.sort(l, new Comparator<ContabilitaRowV20>() {
                @Override
                public int compare(ContabilitaRowV20 o1, ContabilitaRowV20 o2) {
                    if (o1.data == null || o2.data == null)
                        return 0;
                    return o2.data.compareTo(o1.data);
                }
            });
        }


        FiglioTestata figlioTestata = ApiService.figlioTestata != null ? ApiService.figlioTestata : new FiglioTestata();
        ContoListView listView = (ContoListView) rootView.findViewById(R.id.contoList);
        adapter = new EstrattoContoAdapter(getActivity(), l);
        listView.setAdapter(adapter);

        LinearLayout footer = (LinearLayout) rootView.findViewById(R.id.footer);
        TextView tv_saldo_footer = (TextView) rootView.findViewById(R.id.totale_saldo_value);
        DecimalFormat df = new DecimalFormat("#.00");
        tv_saldo_footer.setText("€ " + df.format(figlioTestata.saldo));
        if (figlioTestata.saldo >= 0) {
            footer.setBackgroundColor(getResources().getColor(R.color.footer_green));
        } else {
            footer.setBackgroundColor(getResources().getColor(R.color.footer_red));
        }
        container = container;
        inflater = inflater;
        refresh();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        ArrayList<ContabilitaRowV20> l = ApiService.estrattoConto != null ? ApiService.estrattoConto : new ArrayList<ContabilitaRowV20>();
        adapter.setData(l);
    }

    private void addLine(TableLayout v){
        View l = new View(getContext());
        l.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
        l.setBackgroundColor(Color.parseColor("#CCCCCC"));
        v.addView(l);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
