package com.oruphones.oruphones_deviceinfo.CustomViewUi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import com.oruphones.oruphones_deviceinfo.R;

public class CPUCircularView extends View {

    private int cpuUsagePercentage = 0;
    private Drawable progressDrawable;

    public CPUCircularView(Context context) {
        super(context);
        init();
    }

    public CPUCircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CPUCircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        progressDrawable = getResources().getDrawable(R.drawable.circle_progress_with_cuts);
    }
    public void setCpuUsagePercentage(int percentage) {
        this.cpuUsagePercentage = percentage;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int progressLevel = (int) (10000 * cpuUsagePercentage / 100);
        progressDrawable.setLevel(progressLevel);
        progressDrawable.setBounds(0, 0, getWidth(), getHeight());
        progressDrawable.draw(canvas);
    }
}
