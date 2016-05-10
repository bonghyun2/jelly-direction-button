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

    public static final int NONE = 791;
    public static final int HORIENTAL = 271;
    public static final int VERTICAL = 694;

    protected Path path;
    protected Paint paint;
    protected float pullDistance;
    protected float defaultRadius;
    private float radius;

    private int mOrientation;


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
        mOrientation = NONE;
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
//        float lRadius = Math.min(Math.abs((float)(pullDistance * 0.4)), radius);
        float lRadius = this.radius == 0 ? defaultRadius : radius;
        PointF smallRoundCenter = getSmallRoundPoint(0, 0, mOrientation);
        PointF largeRoundCenter = getLargeRoundPoint(0, 0, mOrientation);
        canvas.drawCircle(smallRoundCenter.x, smallRoundCenter.y, sRadius, paint);
        if(mOrientation == NONE) return;
        canvas.drawCircle(largeRoundCenter.x, largeRoundCenter.y, lRadius, paint);

        path.reset();
        if(mOrientation == HORIENTAL) {
            path.moveTo(smallRoundCenter.x , smallRoundCenter.y - sRadius);
            path.quadTo((largeRoundCenter.x + smallRoundCenter.x) / 2, smallRoundCenter.y - sRadius,  largeRoundCenter.x , largeRoundCenter.y - lRadius);
            path.lineTo(largeRoundCenter.x , largeRoundCenter.y + lRadius);
            path.quadTo((largeRoundCenter.x + smallRoundCenter.x) / 2, smallRoundCenter.y + sRadius,  smallRoundCenter.x , largeRoundCenter.y + lRadius);
        } else {
            path.moveTo(smallRoundCenter.x - sRadius, smallRoundCenter.y);
            path.quadTo(smallRoundCenter.x - sRadius, (largeRoundCenter.y + smallRoundCenter.y) / 2, largeRoundCenter.x - lRadius, largeRoundCenter.y);
            path.lineTo(largeRoundCenter.x + lRadius, largeRoundCenter.y);
            path.quadTo(smallRoundCenter.x + sRadius, (largeRoundCenter.y + smallRoundCenter.y) / 2, smallRoundCenter.x + sRadius, smallRoundCenter.y);
        }

        canvas.drawPath(path, paint);
    }


    PointF getSmallRoundPoint(float x, float y, int orientation) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + ((orientation == VERTICAL) ? getTranslationX() : 0);
//        Log.d("getSmallRoundPoint" , "offsetX ::" + offsetX);
        float offsetY = getMeasuredHeight()/2 + ((orientation == HORIENTAL) ? getTranslationY() : 0);
        ret.x = offsetX + x;
        ret.y = offsetY + y;
        return ret;
    }

    PointF getSmallRoundPoint(PointF pointF, int orientation) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + ((orientation == VERTICAL) ? getTranslationX() : 0);
        float offsetY = getMeasuredHeight()/2 + ((orientation == HORIENTAL) ? getTranslationY() : 0);
        ret.x = offsetX + pointF.x;
        ret.y = offsetY + pointF.y;
        return ret;
    }

    PointF getLargeRoundPoint(float x, float y, int orientation) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + ((orientation == VERTICAL) ? getTranslationX() : pullDistance);
        float offsetY = getMeasuredHeight()/2 + ((orientation == HORIENTAL) ? getTranslationY() : pullDistance);;
        ret.x = offsetX + x;
        ret.y = offsetY + y;
        return ret;
    }

    PointF getLargeRoundPoint(PointF pointF, int orientation) {
        PointF ret = new PointF();
        float offsetX = getMeasuredWidth()/2 + ((orientation == VERTICAL) ? getTranslationX() : pullDistance);
        float offsetY = getMeasuredHeight()/2 + ((orientation == HORIENTAL) ? getTranslationY() : pullDistance);;
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

    public void setPullDistance(float pullDistance) {
        this.pullDistance = pullDistance;
    }

    public float getPullDistance() {
        return pullDistance;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return (this.radius != 0)? this.radius : defaultRadius;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public void setmOrientation(int orientation) {
        this.mOrientation = orientation;
    }

}
