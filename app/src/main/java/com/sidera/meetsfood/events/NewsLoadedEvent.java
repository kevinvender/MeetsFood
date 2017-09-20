package com.sidera.meetsfood.events;

import com.sidera.meetsfood.api.beans.ContabilitaRow;
import com.sidera.meetsfood.api.beans.News;

import java.util.ArrayList;

public class NewsLoadedEvent {

    public ArrayList<News> news;

    public NewsLoadedEvent(ArrayList<News> news) {
        this.news = news;
    }

}
