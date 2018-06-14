package com.huxq17.nestscroll;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class CollapsingTitleView extends FrameLayout {
    private Toolbar mToolbar;
    private View mCollapsingView;
    private int mToolbarHeight, mHeight;
    private int mVerticalOffset;
    private int mTotalScrollRange;

    private int[] mFakeConsumed = new int[2];

    public CollapsingTitleView(@NonNull Context context) {
        this(context, null);
    }

    public CollapsingTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCollapsingView = getChildAt(0);
        mToolbar = (Toolbar) getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mToolbarHeight = mToolbar.getMeasuredHeight();
        mHeight = getMeasuredHeight();
        mTotalScrollRange = mHeight - mToolbarHeight;
    }

    protected void onLayoutChanged() {
        mToolbarHeight = mToolbar.getHeight();
        mHeight = getHeight();
        mTotalScrollRange = mHeight - mToolbarHeight;
        notifyBottomChanged();
        notifyOffsetChanged();
        int collapsingViewTop = mCollapsingView.getTop();
        if (collapsingViewTop != 0) {
            ViewCompat.offsetTopAndBottom(mCollapsingView, 0 - collapsingViewTop);
        }
        if (mVerticalOffset != 0) {
            ViewCompat.offsetTopAndBottom(mCollapsingView, mVerticalOffset);
        }
    }

    public int getMinHeight() {
        return mToolbarHeight;
    }

    public boolean canCollapse() {
        return mHeight + mVerticalOffset > mToolbarHeight;
    }

    public boolean canStretch() {
        return mVerticalOffset < 0;
    }

    public int getVerticalOffset() {
        return mVerticalOffset;
    }

    public int getTotalScrollRange() {
        return mTotalScrollRange;
    }

    public boolean shouldCollapse() {
        return mHeight / 2 + mVerticalOffset < mToolbarHeight;
    }

    protected void scroll(int dy) {
        scroll(dy, mFakeConsumed);
    }

    protected void scroll(int dy, int[] consumed) {
        //如果dy>0并且titleView可以收缩，则收缩titleView
        int offset = 0;
        if (dy > 0 && canCollapse()) {
            offset = -dy;
            //如果收缩过度
            if (mHeight + mVerticalOffset + offset < mToolbarHeight) {
                offset = mToolbarHeight - mHeight - mVerticalOffset;
            }
        }
        //如果dy<0并且titleView可以拉伸，则拉伸titleView
        if (dy < 0 && canStretch()) {
            offset = -dy;
            //如果拉伸过度
            if (mVerticalOffset + offset > 0) {
                offset = -mVerticalOffset;
            }
        }
        mVerticalOffset += offset;
        consumed[1] = -offset;
//        LogUtils.e("dy=" + dy + ";offset=" + offset + "; mToolbarHeight=" + mToolbarHeight + ";mHeight + mVerticalOffset=" + (mHeight + mVerticalOffset) + ";mHeight=" + mHeight);
        if (offset == 0) return;
        ViewCompat.offsetTopAndBottom(mCollapsingView, offset);
//        LogUtils.e(" offset behind mCollapsingView.top=" + mCollapsingView.getTop() + ";mVerticalOffset=" + mVerticalOffset);
        notifyBottomChanged();
        notifyOffsetChanged();
    }

    private void notifyBottomChanged() {
        if (mOnBottomChangedListener != null) {
            mOnBottomChangedListener.onHeaderBottomChanged(mHeight + mVerticalOffset);
        }
    }

    private void notifyOffsetChanged() {
        if (mOnOffsetChangedListener != null) {
            mOnOffsetChangedListener.onOffsetChanged(this, mVerticalOffset);
        }
    }

    private OnBottomChangedListener mOnBottomChangedListener;

    public void setOnBottomChangedListener(OnBottomChangedListener bottomChangedListener) {
        mOnBottomChangedListener = bottomChangedListener;
    }

    public interface OnBottomChangedListener {
        void onHeaderBottomChanged(int bottom);
    }

    private OnOffsetChangedListener mOnOffsetChangedListener;

    public void setOnOffsetChangedListener(OnOffsetChangedListener onOffsetChangedListener) {
        mOnOffsetChangedListener = onOffsetChangedListener;
    }

    public interface OnOffsetChangedListener {
        void onOffsetChanged(CollapsingTitleView collapsingTitleView, int verticalOffset);
    }

}
