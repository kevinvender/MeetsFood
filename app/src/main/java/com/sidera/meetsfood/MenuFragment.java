package com.sidera.meetsfood;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.Menu;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Date today = new Date();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        System.out.println("oc");
    }

    Button menu_set_date;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println("ocv");
        final View layout = inflater.inflate(R.layout.fragment_menu, container, false);
        // Inflate the layout for this fragment

        menu_set_date = (Button) layout.findViewById(R.id.menu_set_date);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(ApiService.dataMenu);
        menu_set_date.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        menu_set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(layout.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(year, monthOfYear, dayOfMonth);
                        menu_set_date.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
                        Log.e("menu", "reload->" + calendar.getTime());
                        ChildDetailActivity act = (ChildDetailActivity) getActivity();
                        act.reloadMenuEvent(calendar.getTime());
                        act.progressDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

                dialog.show();
            }
        });

        LinearLayout ll = (LinearLayout)layout.findViewById(R.id.menu_layout_data);
        ll.removeAllViews();
        layout.invalidate();
        //Log.e("MENU","child:::"+ ll.getChildCount());
        ArrayList<Menu> aMenu = ApiService.menu != null ? ApiService.menu : new ArrayList<Menu>();

        for(Menu m : aMenu){
            // Log.e("MENU",m.piatto);
            //Log.e("MENU", m.testo);
            CardView card = (CardView) inflater.inflate(R.layout.menu_view, container,false);
            ((TextView)card.findViewById(R.id.menu_titolo)).setText(m.piatto);
            ((TextView)card.findViewById(R.id.menu_descrizione)).setText(m.testo);
            ll.addView(card);
        }

        //Log.e("MENU", "child:::" + ll.getChildCount());

     /*   EditText primo = (EditText) layout.findViewById(R.id.menu_primo);
        primo.setText("Pasta al pomodoro");
        EditText secondo = (EditText) layout.findViewById(R.id.menu_secondo);
        primo.setText("Pasta al pomodoro");
        EditText contorno = (EditText) layout.findViewById(R.id.menu_contorno);
        primo.setText("Pasta al pomodoro");
        EditText dessert = (EditText) layout.findViewById(R.id.menu_dessert);
        primo.setText("Pasta al pomodoro");
*/
        return layout;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
