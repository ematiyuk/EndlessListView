package com.github.ematiyuk.endlesslistview;

import android.content.Context;
import android.support.v7.view.ContextThemeWrapper;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class ThemedNumberPicker extends NumberPicker {

    public ThemedNumberPicker(Context context) {
        this(context, null);
    }

    public ThemedNumberPicker(Context context, AttributeSet attrs) {
        // wrap the current context in the style we defined before
        super(new ContextThemeWrapper(context, R.style.NumberPickerTextColorStyle), attrs);
    }
}