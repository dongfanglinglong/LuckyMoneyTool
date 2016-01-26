package com.df.moneytool.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.df.moneytool.Gloable;

/**
 * Created by chenfu on 2016/1/26.
 */
public final class PreferenceUtil {
    private static final String KEY_PREFERENCE_FINENAME = "preference";

    public static final String KEY_PREFERENCE_NOTIFICATION = "p_notification";
    public static final String KEY_PREFERENCE_SCROLLED = "p_scrolled";
    public static final String KEY_PREFERENCE_OPEN_LUCKYMONEY = "p_luckymoney";
    public static final String KEY_PREFERENCE_SAVE_LOGGER = "p_logger";


    private PreferenceUtil() {
    }

    public static boolean init(Context context) {

        SharedPreferences preferences = context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE);

        if (preferences.getBoolean(KEY_PREFERENCE_FINENAME, false)) {
            Gloable.MONITOR_NOTIFICATION = preferences.getBoolean(KEY_PREFERENCE_NOTIFICATION, false);
            Gloable.MONITOR_SCROLLED = preferences.getBoolean(KEY_PREFERENCE_SCROLLED, true);
            Gloable.MONITOR_OPEN_LUCKYMONEY = preferences.getBoolean(KEY_PREFERENCE_OPEN_LUCKYMONEY, true);
            Gloable.MONITOR_SAVE_LOGGER = preferences.getBoolean(KEY_PREFERENCE_SAVE_LOGGER, false);
            return true;
        }
        else {
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean(KEY_PREFERENCE_NOTIFICATION, false);
            editor.putBoolean(KEY_PREFERENCE_SCROLLED, true);
            editor.putBoolean(KEY_PREFERENCE_OPEN_LUCKYMONEY, true);
            editor.putBoolean(KEY_PREFERENCE_SAVE_LOGGER, false);

            editor.putBoolean(KEY_PREFERENCE_FINENAME, true);

            return editor.commit();
        }
    }


    public static boolean isMonitorNotification(Context context) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).getBoolean(
                KEY_PREFERENCE_NOTIFICATION, false);
    }


    public static boolean isMonitorScrolled(Context context) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).getBoolean(
                KEY_PREFERENCE_SCROLLED, true);
    }

    public static boolean isMonitorOpenLuckymoney(Context context) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).getBoolean(
                KEY_PREFERENCE_OPEN_LUCKYMONEY, true);
    }

    public static boolean isMonitorSaveLogger(Context context) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).getBoolean(
                KEY_PREFERENCE_SAVE_LOGGER, false);
    }


    public static boolean setMonitorNotification(Context context, boolean b) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).edit()
                .putBoolean(KEY_PREFERENCE_NOTIFICATION, b).commit();
    }

    public static boolean setMonitorScrolled(Context context, boolean b) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).edit()
                .putBoolean(KEY_PREFERENCE_SCROLLED, b).commit();
    }

    public static boolean setMonitorOpenLuckymoney(Context context, boolean b) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).edit()
                .putBoolean(KEY_PREFERENCE_OPEN_LUCKYMONEY, b).commit();
    }

    public static boolean setMonitorSaveLogger(Context context, boolean b) {
        return context.getSharedPreferences(KEY_PREFERENCE_FINENAME, Context.MODE_PRIVATE).edit()
                .putBoolean(KEY_PREFERENCE_SAVE_LOGGER, b).commit();
    }
}
