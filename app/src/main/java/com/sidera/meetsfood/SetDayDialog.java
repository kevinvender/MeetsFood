package com.sidera.meetsfood;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.sidera.R;
import com.sidera.meetsfood.view.materialcalendarview.CalendarDay;

/**
 * Created by cristian.stenico on 25/09/2015.
 */
public class SetDayDialog extends DialogFragment {
    int choice = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        int selected = getArguments().getInt("presenza");
        Log.e("PRESENZA", String.valueOf(selected));
        builder.setTitle(getResources().getString(R.string.choose_option_for_date) + " " + getArguments().getString("date"))
                .setSingleChoiceItems(new CharSequence[]{getResources().getString(R.string.consumazione),getResources().getString(R.string.assenze), getResources().getString(R.string.pasti_in_bianco)}, selected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
                    }
                });
        return builder.create();
    }

}
