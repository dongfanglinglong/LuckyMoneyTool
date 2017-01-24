package com.marswin89.marsdaemon.nativ;

import android.content.Context;
import android.util.Log;

import com.marswin89.marsdaemon.NativeDaemonBase;

/**
 * native code to watch each other when api over 21 (contains 21)
 *
 * @author Mars
 */
public class NativeDaemonAPI21 extends NativeDaemonBase {

    static {
        try {
            System.loadLibrary("daemon_api21");
        } catch (Exception e) {
            Log.e("", "", e);
        }
    }

    public NativeDaemonAPI21(Context context) {
        super(context);
    }

    public native void doDaemon(String indicatorSelfPath, String indicatorDaemonPath, String observerSelfPath, String observerDaemonPath);
}
