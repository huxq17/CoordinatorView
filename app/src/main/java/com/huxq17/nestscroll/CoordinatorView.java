package com.huxq17.nestscroll;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.huxq17.handygridview.scrollrunner.ICarrier;
import com.huxq17.handygridview.scrollrunner.ScrollRunner;

import static android.support.v4.view.ViewCompat.TYPE_NON_TOUCH;
import static android.support.v4.view.ViewCompat.TYPE_TOUCH;


public class CoordinatorView extends FrameLayout implements NestedScrollingParent2, ICarrier, CollapsingTitleView.OnBottomChangedListener {
    private CollapsingTitleView mTitleView;
    private View mScrollingView;
    private boolean mFling = false;
    private ScrollRunner mScrollRunner;

    public CoordinatorView(@NonNull Context context) {
        this(context, null);
    }

    public CoordinatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mScrollRunner = new ScrollRunner(this);
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mTitleView.onLayoutChanged();
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTitleView = (CollapsingTitleView) getChildAt(0);
        mTitleView.setOnBottomChangedListener(this);
        mScrollingView = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildrenWithMargins(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    private void measureChildrenWithMargins(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = getChildCount();
        for (int i = 0; i < size; ++i) {
            final View child = getChildAt(i);
            int heightUsed = 0;
            if (child.getVisibility() != GONE) {
                if (child.getClass() != CollapsingTitleView.class) {
                    heightUsed = mTitleView.getMinHeight();
                }
                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            }
        }
    }

    @Override
    public void onHeaderBottomChanged(int bottom) {
        int scrollingViewTop = mScrollingView.getTop();
        if (bottom != scrollingViewTop) {
            ViewCompat.offsetTopAndBottom(mScrollingView, bottom - scrollingViewTop);
        }
    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (dy > 0 && mTitleView.canCollapse()) {
            mTitleView.scroll(dy, consumed);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if (dyUnconsumed < 0 && mTitleView.canStretch()) {
            mTitleView.scroll(dyUnconsumed);
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (type == TYPE_TOUCH) {
            mFling = false;
        }
        mScrollRunner.cancel();
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        if (type == TYPE_NON_TOUCH || !mFling) {
            //关闭或完全打开titleView
            mFling = false;
            int verticalOffset = mTitleView.getVerticalOffset();
            int totalScrollRange = mTitleView.getTotalScrollRange();
            int titleHeight = mTitleView.getHeight();
            if (verticalOffset == 0 || Math.abs(verticalOffset) == totalScrollRange) {
                return;
            }
            int offset;
            if (!mTitleView.shouldCollapse()) {
                //完全打开
                offset = verticalOffset;
            } else {
                //关闭
                offset = totalScrollRange + verticalOffset;
            }
            if (offset != 0) {
                mScrollRunner.start(0, offset, computeScrollVerticalDuration(offset, totalScrollRange / 2));
            }
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        boolean consumed = super.onNestedPreFling(target, velocityX, velocityY);
        if (!consumed) {
            mFling = true;
        } else {
            mFling = false;
        }
        return consumed;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

    }


    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {

        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onMove(int lastX, int lastY, int curX, int curY) {
        int dy = curY - lastY;
        if (dy != 0) {
            mTitleView.scroll(dy);
        }
    }

    @Override
    public void onDone() {
    }

    public int computeScrollVerticalDuration(int dy, int height) {
        final int duration;
        float absDelta = (float) Math.abs(dy);
        duration = (int) (((absDelta / height) + 1) * 100);
        return dy == 0 ? 0 : duration;
    }
}
