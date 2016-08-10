package com.eeec.GestionEspresso.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.eeec.GestionEspresso.R;

public class KTextView extends TextView
{
    public KTextView(Context context)
    {
        super(context);
    }

    public KTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public KTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs)
    {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.KTextView);
        String customFont = a.getString(R.styleable.KTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset)
    {
        Typeface typeface;
        try
        {
            typeface = Typeface.createFromAsset(ctx.getAssets(), asset);
        }
        catch (Exception e)
        {
            return false;
        }
        setTypeface(typeface);
        return true;
    }
}
