package com.sidera.meetsfood;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment{

    DatePickerDialog.OnDateSetListener onDateSet;
    private boolean isModal = false;

    public static DatePickerFragment newInstance()
    {
        DatePickerFragment frag = new DatePickerFragment();
        frag.isModal = true; // WHEN FRAGMENT IS CALLED AS A DIALOG SET FLAG
        return frag;
    }

    public DatePickerFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        String data = bundle.getString("data", "2016-01-01");

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(data.substring(0, 4)),Integer.parseInt(data.substring(5,7)) -1,Integer.parseInt(data.substring(8,10)));
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), onDateSet, year, month, day);
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSet = onDate;
    }
}
