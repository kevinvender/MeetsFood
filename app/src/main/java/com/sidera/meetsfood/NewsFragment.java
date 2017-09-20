package com.sidera.meetsfood;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sidera.R;
import com.sidera.meetsfood.api.ApiService;
import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.FiglioTestata;
import com.sidera.meetsfood.api.beans.News;

import java.util.ArrayList;
import java.util.List;


public class NewsFragment extends Fragment {
    public NewsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.card_layout);
        ll.removeAllViews();
        ArrayList<News> news = ApiService.news != null ? ApiService.news : new ArrayList<News>();

        for(News n : news){
            CardView card = (CardView) inflater.inflate(R.layout.news_view, container,false);
            ((TextView)card.findViewById(R.id.title_text)).setText(n.titolo);
            ((TextView)card.findViewById(R.id.content_text)).setText(n.testo);
            ((TextView)card.findViewById(R.id.from_text)).setText("");

            ll.addView(card);
        }
        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
