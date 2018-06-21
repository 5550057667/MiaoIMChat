package com.sky_wf.chinachat.utils;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * @Date : 2018/6/21
 * @Author : WF
 * @Description :
 */
public class SoftKeyboardStateHelper implements ViewTreeObserver.OnGlobalLayoutListener
{
    private int screenHeight;
    private int softHeight;
    private int scrollHeight;
    int location[];
    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private final View childView;
    private int lastSoftKeyboardHeightInPx;
    private boolean isSoftKeyboardOpened;

    public interface SoftKeyboardStateListener
    {
        void onSoftKeyboardOpened(int keyboardHeightInPx);

        void onSoftKeyboardClosed();
    }

    public SoftKeyboardStateHelper(View activityRootView, View childView)
    {
        this(activityRootView, childView, false);
    }

    public SoftKeyboardStateHelper(View activityRootView, View childView,
            boolean isSoftKeyboardOpened)
    {
        this.activityRootView = activityRootView;
        this.childView = childView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout()
    {
//        Rect rect = new Rect();
//        activityRootView.getWindowVisibleDisplayFrame(rect);
//        int heightDiff = activityRootView.getRootView().getHeight() -(rect.bottom - rect.top);
//        if(!isSoftKeyboardOpened&&heightDiff >120)
//        {
//            isSoftKeyboardOpened = true;
//            notifyOnSoftKeyboardOpened(heightDiff);
//        }else if(isSoftKeyboardOpened&& heightDiff<120)
//        {
//            isSoftKeyboardOpened = false;
//            notifyOnSoftKeyboardClosed();
//        }

        final Rect r = new Rect();
        // r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        if (location == null)
        {
            location = new int[2];
            childView.getLocationOnScreen(location);
        }
        screenHeight = activityRootView.getRootView().getHeight();
        softHeight = screenHeight - r.bottom;
        if (scrollHeight == 0 && softHeight > 120)
        {
            scrollHeight = location[1] + childView.getHeight() - (screenHeight - softHeight);
        }
        if (softHeight > 120 && !isSoftKeyboardOpened)
        {
            if (activityRootView.getScrollY() != scrollHeight)
            {
                Log.d("wftt", "键盘打开情况下滚动>>>>");
//                activityRootView.scrollTo(0,scrollHeight);
                scrollToPos(activityRootView, 0, scrollHeight);
                isSoftKeyboardOpened = true;
            }
        } else

        {

            if (activityRootView.getScrollY() != 0 && isSoftKeyboardOpened)
            {
                Log.d("wftt", "键盘关闭情况下滚动>>>>");
//                activityRootView.scrollTo(0,0);
                scrollToPos(activityRootView, screenHeight, 0);
                isSoftKeyboardOpened = false;
            }

            Log.d("wftt", "location >> height==" + location[1] + "----screenHeight==" + screenHeight
                    + "---softHeight==" + softHeight + "---scrollHeight==" + scrollHeight);
//             final int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom -
//             r.top);
//             if (!isSoftKeyboardOpened && heightDiff > 100)
//             { // if more than 100 pixels, its probably a keyboard...
//             isSoftKeyboardOpened = true;
//              notifyOnSoftKeyboardOpened(heightDiff);
//             } else if (isSoftKeyboardOpened && heightDiff < 100)
//             {
//             isSoftKeyboardOpened = false;
//              notifyOnSoftKeyboardClosed();
//             }
        }

    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened)
    {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened()
    {
        return isSoftKeyboardOpened;
    }

    /**
     * Default value is zero (0)
     * 
     * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeightInPx()
    {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener)
    {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(SoftKeyboardStateListener listener)
    {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx)
    {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;

        for (SoftKeyboardStateListener listener : listeners)
        {
            if (listener != null)
            {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed()
    {
        for (SoftKeyboardStateListener listener : listeners)
        {
            if (listener != null)
            {
                listener.onSoftKeyboardClosed();
            }
        }
    }

    private void scrollToPos(final View view, int start, int end)
    {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                view.scrollTo(0, (Integer) animation.getAnimatedValue());
            }
        });
        animator.start();

    }
}