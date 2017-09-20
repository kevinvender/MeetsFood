package com.sidera.meetsfood.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.FiglioTestata;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class EstrattoContoAdapter extends ArrayAdapter<ContabilitaRow> {
    private LayoutInflater inflater;
    ArrayList<ContabilitaRow> list;

    public EstrattoContoAdapter(Context context, ArrayList<ContabilitaRow> list) {
        super(context, 0, list);
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }


    public void setData(ArrayList<ContabilitaRow> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ContabilitaRow getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ContabilitaRow r = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.conto_row_layout, parent, false);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        DecimalFormat df = new DecimalFormat("#.00");

        // Lookup view for data population
        ((TextView) convertView.findViewById(R.id.contoDataText)).setText((r.data != null) ? dateFormat.format(r.data) : "");
        ((TextView) convertView.findViewById(R.id.contoServizioText)).setText(r.deno);
        if (r.tipo.equals("P")) {
            ((TextView) convertView.findViewById(R.id.contoSaldoTxt)).setText((r.importo != null) ? "€ -" + df.format(r.importo) : "");
            ((TextView) convertView.findViewById(R.id.contoSaldoTxt)).setTextColor(getContext().getResources().getColor(R.color.footer_red));
        } else if (r.tipo.equals("R")) {
            ((TextView) convertView.findViewById(R.id.contoSaldoTxt)).setText((r.importo != null) ? "€ +" + df.format(r.importo) : "");
            ((TextView) convertView.findViewById(R.id.contoSaldoTxt)).setTextColor(getContext().getResources().getColor(R.color.footer_green));
        }

        // Return the completed view to render on screen
        return convertView;
    }
}