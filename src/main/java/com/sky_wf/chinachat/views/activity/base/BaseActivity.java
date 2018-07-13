package com.sky_wf.chinachat.views.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.roger.catloadinglibrary.CatLoadingView;
import com.sky_wf.chinachat.utils.SoftKeyboardStateHelper;
import com.sky_wf.chinachat.utils.Utils;

/**
 * @Date : 2018/5/15
 * @Author : WF
 * @Description :
 */
public abstract class BaseActivity extends AppCompatActivity
{
    private CatLoadingView catLoadingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        catLoadingView = new CatLoadingView();
        initView();
        initTitle();
        setListener();
    }


    public void startTargetActivity(Activity activity)
    {
        Intent intent = new Intent(this, activity.getClass());
        startActivity(intent);
    }

    protected abstract void initTitle();

    protected abstract void initView();

    protected abstract void setListener();

    protected boolean isNetAvaliable()
    {
        return Utils.isNetAvaliable(this);
    }

    public void hideKeyBoard()
    {
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getWindow()
                .getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        {
            if (getCurrentFocus() != null)
            {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    protected void autoScroll(final View root, final View childView)
    {
        root.getViewTreeObserver()
                .addOnGlobalLayoutListener(new SoftKeyboardStateHelper(root, childView));

    }

    protected void showDialog(String txt)
    {
        if (catLoadingView != null)
        {
            if (txt != null && txt.length() > 0)
            {
                catLoadingView.setText(txt);
            }
            catLoadingView.show(getSupportFragmentManager(), "");
        }
    }

    protected void hideDialog()
    {
        if (catLoadingView != null)
        {
            catLoadingView.dismiss();
            catLoadingView = null;
        }
    }

}
