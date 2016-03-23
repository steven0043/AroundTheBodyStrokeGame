package com.atbsg.atbsg.menu;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atbsg.atbsg.R;

/**
 * Created by Steven on 20/01/2016.
 *
 * A Wearable List class used for all lists.
 */
public class WearableList extends LinearLayout
        implements WearableListView.OnCenterProximityListener {

    private ImageView listCircle;
    private TextView listItem;

    private final int listItemUnselectedColour;
    private final int listItemSelectedColour;

    public WearableList(Context context) {
        this(context, null);
    }

    public WearableList(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableList(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);

        listItemUnselectedColour = getResources().getColor(R.color.grey);
        listItemSelectedColour = getResources().getColor(R.color.green);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        listCircle = (ImageView) findViewById(R.id.circle);
        listItem = (TextView) findViewById(R.id.name);
    }

    @Override
    public void onCenterPosition(boolean animate) {
        listItem.setAlpha(1f);
        ((GradientDrawable) listCircle.getDrawable()).setColor(listItemSelectedColour);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        ((GradientDrawable) listCircle.getDrawable()).setColor(listItemUnselectedColour);
        listItem.setAlpha(.25f);
    }
}