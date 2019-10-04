package com.example.appinsta;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

/**
 * Created by OT on 08/06/2017.
 */

public class CustomView extends LinearLayout {



    TextView number;
    TextView condition;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.custom_view, this);

        int[] sets = {R.attr.Number};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        String number = (String) typedArray.getText(0);

        int[] set = {R.attr.Condition};
        TypedArray typedArray2 = context.obtainStyledAttributes(attrs, set);
        String condition = (String) typedArray2.getText(0);


        initComponents();

        setNumberText(number);
        setConditionText(condition);

    }

    private void initComponents() {

        number = (TextView) findViewById(R.id.number);

        condition = (TextView) findViewById(R.id.condition);

    }

    public CharSequence getArtistText() {
        return number.getText();
    }

    public void setNumberText(String value) {
        number.setText(value);
    }

    public CharSequence getTrackText() {
        return condition.getText();
    }

    public void setConditionText(String value) {
        condition.setText(value);
    }

}
