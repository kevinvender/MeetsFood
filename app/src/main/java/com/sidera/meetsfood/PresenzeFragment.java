package com.sidera.meetsfood;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sidera.R;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.events.BusProvider;
import com.sidera.meetsfood.events.PresenzeLoadedEvent;
import com.sidera.meetsfood.view.materialcalendarview.CalendarDay;
import com.sidera.meetsfood.view.materialcalendarview.DayViewDecorator;
import com.sidera.meetsfood.view.materialcalendarview.DayViewFacade;
import com.sidera.meetsfood.view.materialcalendarview.MaterialCalendarView;
import com.sidera.meetsfood.view.materialcalendarview.OnDateChangedListener;
import com.sidera.meetsfood.view.materialcalendarview.OnMonthChangedListener;
import com.sidera.meetsfood.view.materialcalendarview.spans.DotSpan;
import com.sidera.meetsfood.view.materialcalendarview.spans.RoundedBackgroundSpan;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
    private int actualMonth;
    private CalendarDay dayToSet;
    public static final int DIALOG_FRAGMENT = 1;
    private MaterialCalendarView calendarView;
    private Bus bus;

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

        actualMonth = CalendarDay.from(ApiService.presenzeMonth).getMonth();

        ArrayList<ContabilitaRow> presenze = ApiService.presenze;
        if (presenze != null) {
            Log.e("PRESENZE", String.valueOf(presenze.size()));
            for(ContabilitaRow r: presenze) {
                Calendar c = Calendar.getInstance();
                c.setTime(r.data);
                if (r.pasto_bianco.equals("0"))
                    this.presenze.add(c.get(Calendar.DATE));
                else
                    this.inBianco.add(c.get(Calendar.DATE));
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDaysInfo();
        View rootView = inflater.inflate(R.layout.fragment_presenze, container, false);
        final AppCompatActivity act = (AppCompatActivity)getActivity();
        calendarView = (MaterialCalendarView)rootView.findViewById(R.id.calendarView);
        configCalendar(calendarView);

        return rootView;
    }

    private void configCalendar(MaterialCalendarView calendarView){
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
        calendarView.setSelectionColor(ContextCompat.getColor(getContext(),R.color.background));
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
                }
                widget.invalidateDecorators();
            }
        });
        calendarView.setFirstDayOfWeek(2);
        DayViewDecorator decoratorToday = new DayViewDecorator() {
            int today;
            int bgColor =  ContextCompat.getColor(getContext(),R.color.ColorPrimaryDark);
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
                if(presenze.contains(today))
                    bgColor = ContextCompat.getColor(getContext(),R.color.green);
                else if(assenze.contains(today))
                    bgColor = ContextCompat.getColor(getContext(),R.color.red);
                else if(inBianco.contains(today))
                    bgColor = ContextCompat.getColor(getContext(),R.color.ColorPrimary);
                ShapeDrawable shape = new ShapeDrawable(new OvalShape());
                shape.setShaderFactory(new ShapeDrawable.ShaderFactory() {
                    @Override
                    public Shader resize(int width, int height) {
                        return new LinearGradient(0, 0, 0, 0, bgColor, bgColor, Shader.TileMode.REPEAT);
                    }
                });
                view.setSelectionDrawable(shape);
                view.addSpan(new ForegroundColorSpan(Color.WHITE));
            }
        };
        DayViewDecorator decoratorInBianco = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(inBianco.contains(day.getDay()))
                    return true;
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.ColorPrimary)));
            }
        };
        DayViewDecorator decoratorPresenze = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(presenze.contains(day.getDay()))
                    return true;
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
               view.addSpan(new RoundedBackgroundSpan(35, ContextCompat.getColor(getContext(), R.color.green), ContextCompat.getColor(getContext(), R.color.white)));
                // view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(),R.color.green)));
            }
        };
        DayViewDecorator decoratorAssenze = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                if(assenze.contains(day.getDay()))
                    return true;
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.red)));
            }
        };
        calendarView.addDecorators(decoratorPresenze, decoratorAssenze, decoratorInBianco, decoratorToday);
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
