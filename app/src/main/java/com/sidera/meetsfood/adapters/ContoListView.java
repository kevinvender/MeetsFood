package com.sidera.meetsfood.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by kevin.vender on 02/10/2017.
 */
public class ContoListView  extends ListView {

    public ContoListView  (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContoListView  (Context context) {
        super(context);
    }

    public ContoListView  (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}