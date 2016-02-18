package com.bnystudio.library.jellydirectionbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Bonghyun on 2015-12-19.
 */
public class JellyDirectionLayout extends FrameLayout{

    protected Path path;
    protected Paint paint;
    protected float pullHeight;
    protected float defaultRadius;
    private boolean isMouseDown;
    private float radius;

    public JellyDirectionLayout(Context context) {
        this(context, null, 0);
    }

    public JellyDirectionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JellyDirectionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setWillNotDraw(false);
        if (isInEditMode()) {
            return;
        }
        path = new Path();
        paint = new Paint();
        paint.setColor(getContext().getResources().getColor(android.R.color.holo_blue_bright));
        paint.setAntiAlias(true);
    }

    public void setJellyColor(int jellyColor) {
        paint.setColor(jellyColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawJelly(canvas);
    }

    private void drawJelly(Canvas canvas) {
        float sRadius = this.radius == 0 ? defaultRadius : radius;
        float lRadius = Math.max(Math.abs((float)(pullHeight * 0.4)), radius);
        PointF smallRoundCenter = getSmallRoundPoint(0, 0);
        PointF largeRoundCenter = getLargeRoundPoint(0,0);
        canvas.drawCircle(smallRoundCenter.x, smallRoundCenter.y, sRadius, paint);
        canvas.drawCircle(largeRoundCenter.x, largeRoundCenter.y, lRadius, paint);

        path.reset();
        path.moveTo(smallRoundCenter.x - sRadius, smallRoundCenter.y);
        path.quadTo(smallRoundCenter.x - sRadius, (largeRoundCenter.y + smallRoundCenter.y) / 2, largeRoundCenter.x - lRadius, largeRoundCenter.y);
        path.lineTo(largeRoundCenter.x + lRadius, largeRoundCenter.y);
        path.quadTo(smallRoundCenter.x + sRadius, (largeRoundCenter.y + smallRoundCenter.y) / 2, smallRoundCenter.x + sRadius, smallRoundCenter.y);
        canvas.drawPath(path, paint);
    }


    PointF getSmallRoundPoint(float x, float y) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + getTranslationX();
//        Log.d("getSmallRoundPoint" , "offsetX ::" + offsetX);
        float offsetY = getMeasuredHeight()/2;
        ret.x = offsetX + x;
        ret.y = offsetY + y;
        return ret;
    }

    PointF getSmallRoundPoint(PointF pointF) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + getTranslationX();
        float offsetY = getMeasuredHeight()/2;
        ret.x = offsetX + pointF.x;
        ret.y = offsetY + pointF.y;
        return ret;
    }

    PointF getLargeRoundPoint(float x, float y) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + getTranslationX();
        float offsetY = getMeasuredHeight()/2 + pullHeight;
        ret.x = offsetX + x;
        ret.y = offsetY + y;
        return ret;
    }

    PointF getLargeRoundPoint(PointF pointF) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + getTranslationX();
        float offsetY = getMeasuredHeight()/2;
        ret.x = offsetX + pointF.x;
        ret.y = offsetY + pointF.y;
        return ret;
    }

    public void setDefaultRadius(float radius) {
        this.defaultRadius = radius;
    }

    public float getDefaultRadius() {
        return defaultRadius;
    }

    public void setPullHeight(float pullHeight) {
        this.pullHeight = pullHeight;
    }

    public float getPullHeight() {
        return pullHeight;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return (this.radius != 0)? this.radius : defaultRadius;
    }

}
