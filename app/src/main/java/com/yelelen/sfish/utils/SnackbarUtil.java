package com.yelelen.sfish.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.trycatch.mysnackbar.TSnackbar;
import com.yelelen.sfish.R;

/**
 * Created by yelelen on 17-10-1.
 */

public class SnackbarUtil {
    public static final int TOP_TO_DOWN = TSnackbar.APPEAR_FROM_TOP_TO_DOWN;
    public static final int DOWN_TO_TOP = TSnackbar.APPEAR_FROM_BOTTOM_TO_TOP;

    public static void showNetPrompt(Activity activity, String message) {
        showPrompt(activity, message, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
    }

    public static void showRefreshPrompt(Activity activity, String message) {
        showPrompt(activity, message, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);

    }

    public static void showLoadPrompt(Activity activity, String message) {
       showPrompt(activity, message, TSnackbar.APPEAR_FROM_BOTTOM_TO_TOP);
    }

    public static void showDownloadImagePrompt(Activity activity, String message) {
        showPrompt(activity, message, TSnackbar.APPEAR_FROM_BOTTOM_TO_TOP);
    }

    public static void showSaveImagePrompt(Activity activity, String message) {
        showPrompt(activity, message, TSnackbar.APPEAR_FROM_BOTTOM_TO_TOP);
    }

    public static void showPrompt(Activity activity, String message, int oriention) {
        final View rootView = activity.findViewById(android.R.id.content).getRootView();
        TSnackbar snackbar = TSnackbar.make(rootView, message, Snackbar.LENGTH_SHORT, oriention);
        View v = snackbar.getView();
        v.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
        v.setAlpha(0.75f);
        TextView msg = v.findViewById(R.id.snackbar_text);
        msg.setTextColor(Color.WHITE);
        snackbar.setMinHeight(24, 0);
        snackbar.show();
    }


}
