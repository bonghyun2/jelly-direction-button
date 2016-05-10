package com.bnystudio.library.jellydirectionbutton;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;


/**
 * Created by Bonghyun on 2015-12-20.
 */
public class JellyDirectionButtonLayout extends FrameLayout {
    protected static final int MASK_DIRECTION = 0xff00;
    public static final int SWIPE_TO_NONE = 0x0000;
    public static final int SWIPE_TO_DOWN = 0x0100;
    public static final int SWIPE_TO_UP = 0x0200;
    public static final int SWIPE_TO_RIGHT = 0x0400;
    public static final int SWIPE_TO_LEFT = 0x0800;

    protected static final int MASK_ORIENTATION = 0x00ff;
    protected static final int ORIENTATION_NONE = 0x0000;
    protected static final int ORIENTATION_HORIZONTAL = 0x0001;
    protected static final int ORIENTATION_VERTICAL = 0x0002;

    protected static final float ACTIVE_Y = 30;
    protected static final float ACTIVE_X = 30;

    protected DecelerateInterpolator mDecelerateInterpolator;
    protected ArgbEvaluator mColorEvaluator;
    protected ValueAnimator mReturnAnimation;

    protected JellyDirectionLayout mJellyDirectionLayout;

    protected int mPermittedOrientation = ORIENTATION_NONE;
    protected int mOrientation = ORIENTATION_NONE;
    protected int mDirection = SWIPE_TO_NONE;
    protected int mPrevDirection = SWIPE_TO_NONE;
    protected boolean mIsLock;
    protected boolean mIsProcessing;
    protected boolean mIsActivate;
    protected boolean mPrevIsActivate;
    protected int mNormalColor;
    protected int mUpColor;
    protected int mDownColor;
    protected int mRightColor;
    protected int mLeftColor;
    protected float mTouchStartX;
    protected float mCurrentX;
    protected float mTouchStartY;
    protected float mCurrentY;
    protected OnJellyClickListener mJellyClickListener;

    protected View mChildView;


    public JellyDirectionButtonLayout(Context context) {
        super(context);
        init();
    }

    public JellyDirectionButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
        init();
    }

    public JellyDirectionButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JellyDirectionButtonLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setAttributes(attrs);
        init();
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.JellyDirectionButtonLayout);
        try {
            Resources resources = getResources();
            mNormalColor = a.getColor(R.styleable.JellyDirectionButtonLayout_jellyNormalColor,
                    resources.getColor(android.R.color.holo_blue_bright));
            mDownColor = a.getColor(R.styleable.JellyDirectionButtonLayout_jellyDownColor,
                    resources.getColor(android.R.color.holo_blue_bright));
            mUpColor = a.getColor(R.styleable.JellyDirectionButtonLayout_jellyUpColor,
                    resources.getColor(android.R.color.holo_blue_bright));
            mRightColor = a.getColor(R.styleable.JellyDirectionButtonLayout_jellyRightColor,
                    resources.getColor(android.R.color.holo_blue_bright));
            mLeftColor = a.getColor(R.styleable.JellyDirectionButtonLayout_jellyLeftColor,
                    resources.getColor(android.R.color.holo_blue_bright));
            mPermittedOrientation = a.getInteger(R.styleable.JellyDirectionButtonLayout_jellyOrientation, ORIENTATION_NONE);
        } finally {
            a.recycle();
        }
    }

    private void init() {

        if (isInEditMode()) {
            return;
        }

        if (getChildCount() > 1) {
            throw new RuntimeException("You can only attach one child");
        }

        mIsLock = false;
        mIsProcessing = false;
        mDecelerateInterpolator = new DecelerateInterpolator(10);
        mColorEvaluator = new ArgbEvaluator();

        this.post(new Runnable() {
            @Override
            public void run() {
                int defaultRadius = (int) getDefaultRadius();
                mChildView = getChildAt(0);
                mJellyDirectionLayout = new JellyDirectionLayout(getContext());
                LayoutParams params = new LayoutParams(defaultRadius*2 , defaultRadius*2);
                params.gravity = Gravity.CENTER;
                mJellyDirectionLayout.setLayoutParams(params);
                mJellyDirectionLayout.setDefaultRadius(getDefaultRadius());
                mJellyDirectionLayout.setJellyColor(mNormalColor);
                addView(mJellyDirectionLayout);
                setChildSize();
                if (mChildView != null) {
                    mChildView.bringToFront();
                }

            }
        });
    }

    private float getDefaultRadius() {
        float ret = 0.f;
        float offsetX = Math.min(getMeasuredWidth() / 6, getMeasuredHeight() / 2);
        float offsetY = Math.min(getMeasuredWidth() / 2 , getMeasuredHeight() / 6);
        if(mPermittedOrientation == ORIENTATION_HORIZONTAL) {
            ret = offsetX;
        }else if(mPermittedOrientation == ORIENTATION_VERTICAL) {
            ret = offsetY;
        }else if(mPermittedOrientation == (ORIENTATION_VERTICAL | ORIENTATION_HORIZONTAL)) {
            ret = Math.min(offsetX, offsetY);
        }
        return ret;
    }

    private void setChildSize() {
        if(mChildView == null)  return;
        synchronized (this) {
            LayoutParams cParams = (LayoutParams)mChildView.getLayoutParams();
            cParams.gravity = Gravity.CENTER;
            cParams.width = (int)mJellyDirectionLayout.getRadius() * 2;
            cParams.height = (int)mJellyDirectionLayout.getRadius() * 2;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if(mIsLock) {
            return false;
        }

        if(mIsProcessing) {
            return true;
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = e.getX();
                mTouchStartY = e.getY();

                mCurrentX = mTouchStartX;
                mCurrentY = mTouchStartY;
                if(mReturnAnimation != null) {
                    mReturnAnimation.cancel();
                }
                mPrevDirection = SWIPE_TO_NONE;
                mPrevIsActivate = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentX = e.getX();
                float currentY = e.getY();

                float dx = currentX - mTouchStartX;
                float dy = currentY - mTouchStartY;

                if(Math.abs(dy) > ACTIVE_Y && (mPermittedOrientation & ORIENTATION_VERTICAL) == ORIENTATION_VERTICAL ) {
                    mOrientation = ORIENTATION_VERTICAL;
                    mJellyDirectionLayout.setmOrientation(JellyDirectionLayout.VERTICAL);
                    return true;
                }else if(Math.abs(dx) > ACTIVE_X && (mPermittedOrientation & ORIENTATION_HORIZONTAL) == ORIENTATION_HORIZONTAL) {
                    mOrientation = ORIENTATION_HORIZONTAL;
                    mJellyDirectionLayout.setmOrientation(JellyDirectionLayout.HORIENTAL);
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {
//        Log.d("TouchTest", "Pull :: onTouchEvent :: " + e.getAction());
        if(mIsProcessing) {
            return super.onTouchEvent(e);
        }

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float diff =0;
                mCurrentX = e.getX();
                mCurrentY = e.getY();
                float maxValue = 0.f;
                mPrevDirection = mDirection;
                if(mOrientation == ORIENTATION_HORIZONTAL) {
                    maxValue = getMeasuredWidth()/3;
                    diff = MathUtils.constrains(
                            mCurrentX - mTouchStartX,
                            maxValue);
                    if (diff > 0) {
                        mDirection = SWIPE_TO_RIGHT;
                    }else {
                        mDirection = SWIPE_TO_LEFT;
                    }
                }else{
                    maxValue = getMeasuredHeight()/3;
                    diff = MathUtils.constrains(
                            mCurrentY - mTouchStartY,
                            maxValue);
                    if (diff > 0) {
                        mDirection = SWIPE_TO_DOWN;
                    }else {
                        mDirection = SWIPE_TO_UP;
                    }
                }

                float offset = mDecelerateInterpolator.getInterpolation(Math.abs(diff) / maxValue) * diff;
                float fraction = offset / maxValue;
                updateButtonColor(fraction, mDirection);

                if (mJellyDirectionLayout != null) {
                    updateJellyDirectionLayout(offset, false);
                }
                mPrevIsActivate = mIsActivate;
                mIsActivate = (Math.abs(offset) > maxValue / 2);
                if(mJellyClickListener != null && (mPrevIsActivate != mIsActivate || mPrevDirection != mDirection)) {
                    mJellyClickListener.onReadyClick(this, mDirection, mIsActivate, fraction);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                onTouchFinish(mDirection);
                returnStartPosition();
                return true;
            default:
                return super.onTouchEvent(e);
        }
    }

    protected float getButtonRadius(float offset) {
        return mJellyDirectionLayout.getDefaultRadius() - Math.abs((float)(offset * 0.1));
    }

    private void updateButtonColor(float fraction, int direction) {
        int targetColor = mNormalColor;
        if(direction == SWIPE_TO_DOWN) {
            targetColor = mDownColor;
        }else if(direction == SWIPE_TO_UP) {
            targetColor = mUpColor;
        }
        int color = (int) mColorEvaluator.evaluate(MathUtils.constrains(0, 1, Math.abs(fraction)), mNormalColor, targetColor);

        mJellyDirectionLayout.setJellyColor(color);
    }

    private void onTouchFinish(int direction) {
        mIsProcessing = true;
        if(mJellyClickListener != null && mIsActivate) {
            mJellyClickListener.onClick(this, direction);
        }else{
            setClickFinish();
        }
    }

    private void returnStartPosition() {
        mReturnAnimation = ValueAnimator.ofFloat(mJellyDirectionLayout.getPullDistance(), 0f);
        mReturnAnimation.setDuration(100);
        mReturnAnimation.setInterpolator(new OvershootInterpolator(2));
        mReturnAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateButtonColor(value / mJellyDirectionLayout.getDefaultRadius() , mDirection);
                updateJellyDirectionLayout(value, false);

            }
        });

        mReturnAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                updateJellyDirectionLayout(0, true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                updateJellyDirectionLayout(0, true);
            }

        });
        mReturnAnimation.start();
    }

    public void updateJellyDirectionLayout(float offset, boolean isIdle) {
        float radius = getButtonRadius(offset);
        mJellyDirectionLayout.setRadius(radius);
        mJellyDirectionLayout.setPullDistance(offset);
        setChildSize();
        if(mChildView == null) {
            mJellyDirectionLayout.postInvalidate();
        }else{
            mJellyDirectionLayout.requestLayout();
        }
        setJellyDirectionLayoutSize(isIdle);
    }

    public void setJellyDirectionLayoutSize(boolean isIdle) {
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        if(isIdle) {
            width = getDefaultRadius() * 2;
            height = getDefaultRadius() * 2;
        }
        mJellyDirectionLayout.getLayoutParams().width = (int)width;
        mJellyDirectionLayout.getLayoutParams().height = (int)height;
        mJellyDirectionLayout.requestLayout();
    }

    public int getDirection() {
        return mDirection;
    }

    public void setNormalColor(int normalColor) {
        this.mNormalColor = normalColor;
    }

    public void setUpColor(int upColor) {
        this.mUpColor = upColor;
    }

    public void setDownColor(int downColor) {
        this.mDownColor = downColor;
    }

    public void setRightColor(int rightColor) {
        this.mRightColor = rightColor;
    }

    public void setLeftColor(int leftColor) {
        this.mLeftColor = leftColor;
    }

    public void lock() {
        this.mIsLock = true;
    }

    public void unLock() {
        this.mIsLock = false;
    }



    public void setOnJellyClickListener(OnJellyClickListener listener) {
        mJellyClickListener = listener;
    }


    public interface OnJellyClickListener {
        void onReadyClick(JellyDirectionButtonLayout jellyDirectionButtonLayout, int direction, boolean isActiveState, float fraction);
        void onClick(JellyDirectionButtonLayout jellyDirectionButtonLayout, int direction);
    }

    public void setClickFinish() {
        mIsProcessing = false;
        mIsActivate = false;
    }

}