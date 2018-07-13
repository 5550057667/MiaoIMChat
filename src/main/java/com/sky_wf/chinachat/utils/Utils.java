package com.sky_wf.chinachat.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;

import com.hyphenate.chat.EMMessage;
import com.sky_wf.chinachat.App;
import com.sky_wf.chinachat.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Date : 2018/6/4
 * @Author : WF
 * @Description :
 */
public class Utils
{

    /**
     * 验证手机号
     * 
     * @param mobile
     * @return
     */
    public static boolean isMobileNomber(String mobile)
    {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(17[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

    public static void showLongToast(View view, String msg)
    {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }

    public static void showShortToast(View view, String msg)
    {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 验证是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        java.util.regex.Matcher match = pattern.matcher(str);
        if (match.matches() == false)
        {
            return false;
        } else
        {
            return true;
        }
    }

    /**
     * 判断是否有网络
     * 
     * @param context
     * @return
     */
    public static boolean isNetAvaliable(Context context)
    {
        if (context.checkCallingOrSelfPermission(
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        } else
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null == connectivityManager)
            {
                return false;
            }
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (null == info || !info.isAvailable())
            {
                return false;
            }

        }
        return true;
    }

    /**
     * 获取复制内容
     * 
     * @return
     */
    public static String getCopyText()
    {
        // Android3.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            ClipboardManager clipboardManager = (ClipboardManager) App.context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (null == clipboardManager || !clipboardManager.hasPrimaryClip())
            {
                return "";
            } else
            {
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData == null || clipData.getItemCount() <= 0)
                {
                    return "";
                }
                return clipData.getItemAt(0).getText().toString();
            }

        } else
        {
            android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) App.context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            if (null == clipboardManager || !clipboardManager.hasText())
            {
                return "";
            }
            return clipboardManager.getText().toString();
        }

    }

    public static void finish(Activity activity)
    {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }

    public static int[] getScreenDisplay(Context context)
    {
        int location[] = new int[2];
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        location[0] = wm.getDefaultDisplay().getWidth();
        location[1] = wm.getDefaultDisplay().getHeight();
        return location;
    }

    public static int getScreenHeight(Context mContext)
    {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public static int getScreenWidth(Context mContext)
    {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**设置透明状态栏
     * @param activity
     * @param useThemStatusColor  设置是否使用自定义的状态栏背景颜色
     * @param withLightStatusBar  设置状态栏显示深色字体还是白色字体
     * @param color               设置需要设置的状态栏背景颜色
     */
    public static void setStatusColor(Activity activity, boolean useThemStatusColor,
            boolean withLightStatusBar, int color)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            if (useThemStatusColor)
            {
                activity.getWindow().setStatusBarColor(color);
            } else
            {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            WindowManager.LayoutParams params = activity.getWindow().getAttributes();
            params.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | params.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(withLightStatusBar)
            {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
//            activity.getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

}
