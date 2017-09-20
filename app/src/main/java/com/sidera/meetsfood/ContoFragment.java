package com.sidera.meetsfood;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.adapters.EstrattoContoAdapter;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.FiglioDettagli;
import com.sidera.meetsfood.api.beans.FiglioTestata;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ContoFragment extends Fragment {


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
        ArrayList<ContabilitaRow> l = ApiService.estrattoConto != null ? ApiService.estrattoConto : new ArrayList<ContabilitaRow>();
        FiglioTestata figlioTestata = ApiService.figlioTestata != null ? ApiService.figlioTestata : new FiglioTestata();
        ListView listView = (ListView) rootView.findViewById(R.id.contoList);
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
        ArrayList<ContabilitaRow> l = ApiService.estrattoConto != null ? ApiService.estrattoConto : new ArrayList<ContabilitaRow>();
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
