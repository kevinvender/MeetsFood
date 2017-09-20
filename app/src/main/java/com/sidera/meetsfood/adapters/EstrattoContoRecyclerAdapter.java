package com.sidera.meetsfood.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.api.beans.ContabilitaRow;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class EstrattoContoRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    ArrayList<ContabilitaRow> list;

    public EstrattoContoRecyclerAdapter(Context context, ArrayList<ContabilitaRow> list) {
//        super(context, 0, list);
//        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }


    public void setData(ArrayList<ContabilitaRow> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ContabilitaRow getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public int getCount() {
        return this.list.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ContabilitaRow r = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.conto_row, parent, false);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("#.00");

        // Lookup view for data population
        ((TextView) convertView.findViewById(R.id.dateText)).setText((r.data != null) ? dateFormat.format(r.data) : "");
        ((TextView) convertView.findViewById(R.id.serviceText)).setText(r.tipologia);
        if (r.tipo.equals("P")) {
            ((TextView) convertView.findViewById(R.id.consumoText)).setText((r.importo != null) ? "€" + df.format(r.importo) : "");
            ((TextView) convertView.findViewById(R.id.ricaricaText)).setText("");
        } else if (r.tipo.equals("R")) {
            ((TextView) convertView.findViewById(R.id.consumoText)).setText("");
            ((TextView) convertView.findViewById(R.id.ricaricaText)).setText((r.importo != null) ? "€" + df.format(r.importo) : "");
        }
        // Return the completed view to render on screen
        return convertView;
    }
}