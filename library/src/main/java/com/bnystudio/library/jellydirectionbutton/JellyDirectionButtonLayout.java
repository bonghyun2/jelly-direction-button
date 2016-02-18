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
    public static final int SWIPE_TO_NONE = 0;
    public static final int SWIPE_TO_DOWN = -1;
    public static final int SWIPE_TO_UP = 1;

    protected static final int ANIMATION_DURATION = 200;
    protected static final float ACTIVE_Y = 10;



    protected DecelerateInterpolator mDecelerateInterpolator;
    protected ArgbEvaluator mColorEvaluator;
    protected ValueAnimator mReturnAnimation;

    protected JellyDirectionLayout mJellyDirectionLayout;

    protected int mDirection = SWIPE_TO_NONE;
    protected int mPrevDirection = SWIPE_TO_NONE;
    protected boolean mIsLock;
    protected boolean mIsProcessing;
    protected boolean mIsActivate;
    protected boolean mPrevIsActivate;
    protected int mNormalColor;
    protected int mUpColor;
    protected int mDownColor;
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
                mChildView = getChildAt(0);
                mJellyDirectionLayout = new JellyDirectionLayout(getContext());
                LayoutParams params = new LayoutParams(getMeasuredWidth(), getMeasuredHeight() / 3);
                params.gravity = Gravity.CENTER;
                mJellyDirectionLayout.setLayoutParams(params);
                mJellyDirectionLayout.setDefaultRadius(getMeasuredHeight() / 6);
                mJellyDirectionLayout.setJellyColor(mNormalColor);
                addView(mJellyDirectionLayout);
                setChildSize();
                if (mChildView != null) {
                    mChildView.bringToFront();
                }

            }
        });
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
//        Log.d("TouchTest", "Pull :: onInterceptTouchEvent :: " + e.getAction());
        if(mIsLock) {
            return false;
        }

        if(mIsProcessing) {
            return true;
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartY = e.getY();
                mCurrentY = mTouchStartY;
                if(mReturnAnimation != null) {
                    mReturnAnimation.cancel();
                }
                mPrevDirection = SWIPE_TO_NONE;
                mPrevIsActivate = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = e.getY();
                float dy = currentY - mTouchStartY;
                if(Math.abs(dy) > ACTIVE_Y) {
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

                mCurrentY = e.getY();
                float maxValue = getMeasuredHeight()/3;
                float dy = MathUtils.constrains(
                        mCurrentY - mTouchStartY,
                        maxValue);
                mPrevDirection = mDirection;
                if (dy > 0) {
                    mDirection = SWIPE_TO_DOWN;
                }else {
                    mDirection = SWIPE_TO_UP;
                }

                float offsetY = mDecelerateInterpolator.getInterpolation(Math.abs(dy) / (getMeasuredHeight() / 3)) * dy;
                float fraction = offsetY / maxValue;
                updateButtonColor(fraction, mDirection);

                if (mJellyDirectionLayout != null) {
                    updateJellyDirectionLayout(offsetY, false);
                }
                mPrevIsActivate = mIsActivate;
                mIsActivate = (Math.abs(offsetY) > maxValue / 2);
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

    protected float getButtonRadius(float offsetY) {
        return mJellyDirectionLayout.getDefaultRadius() - Math.abs((float)(offsetY * 0.1));
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
        mReturnAnimation = ValueAnimator.ofFloat(mJellyDirectionLayout.getPullHeight(), 0f);
        mReturnAnimation.setDuration(100);
        mReturnAnimation.setInterpolator(new OvershootInterpolator(2));
        mReturnAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateButtonColor(value / (getMeasuredHeight() / 4), mDirection);
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

    public void updateJellyDirectionLayout(float offsetY, boolean isIdle) {
        float radius = getButtonRadius(offsetY);
        mJellyDirectionLayout.setRadius(radius);
        mJellyDirectionLayout.setPullHeight(offsetY);
        setChildSize();
        if(mChildView == null) {
            mJellyDirectionLayout.postInvalidate();
        }else{
            mJellyDirectionLayout.requestLayout();
        }
        setJellyDirectionLayoutSize(isIdle);
    }

    public void setJellyDirectionLayoutSize(boolean isIdle) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if(isIdle) {
            height /= 3;
        }
        mJellyDirectionLayout.getLayoutParams().width = width;
        mJellyDirectionLayout.getLayoutParams().height = height;
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