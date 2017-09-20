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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.api.beans.FiglioTestata;
import com.sidera.meetsfood.data.Child;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class ChildrenAdapter extends ArrayAdapter<FiglioTestata> {
    private LayoutInflater inflater;
    ArrayList<FiglioTestata> children;

    public ChildrenAdapter(Context context, ArrayList<FiglioTestata> children) {
        super(context, 0, children);
        this.inflater = LayoutInflater.from(context);
        this.children = children;
    }


    public void setData(ArrayList<FiglioTestata> children) {
        this.children = children;
        notifyDataSetChanged();
    }

    @Override
    public FiglioTestata getItem(int position) {
        return this.children.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return this.children.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        FiglioTestata child = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_child, parent, false);
        }
        // Lookup view for data population
        ImageView ivPhoto = (ImageView) convertView.findViewById(R.id.profile_image);
        Bitmap b = BitmapFactory.decodeFile(getContext().getFilesDir().getAbsolutePath() + "/profile_icon" + child.utenza + ".png");
        if(b != null)
            ivPhoto.setImageBitmap(getCircleBitmap(b));
        else
            ivPhoto.setImageBitmap(null);
        TextView tvName = (TextView) convertView.findViewById(R.id.name_label);
        TextView tvCommessa = (TextView) convertView.findViewById(R.id.commessa_label);
        TextView tvServizi = (TextView) convertView.findViewById(R.id.servizi_label);
        TextView tvSaldo = (TextView) convertView.findViewById(R.id.saldo_label);
        TextView tvPasti = (TextView) convertView.findViewById(R.id.pasti_label);
        DecimalFormat df = new DecimalFormat("#.00");
        // Populate the data into the template view using the data object
        tvName.setText(child.display_name);
        tvServizi.setText(child.descr_commessa);
        tvCommessa.setText(child.scuola);
        tvSaldo.setText("€ " + df.format(child.saldo));
        if(child.pasti_totali != 0) {
            tvPasti.setText("Consumati: " + child.pasti_consumati + " pasti di " + child.pasti_totali);
        }else{
            tvPasti.setVisibility(View.GONE);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    private Bitmap getCircleBitmap(Bitmap b){
        final int measure =  b.getWidth() < b.getHeight() ? b.getWidth() : b.getHeight();
        final Bitmap output = Bitmap.createBitmap(measure,
                measure, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, measure, measure);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(b, rect, rect, paint);

        b.recycle();

        return output;
    }
}