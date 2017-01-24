package com.marswin89.marsdaemon.nativ;

import android.content.Context;
import android.util.Log;

import com.marswin89.marsdaemon.NativeDaemonBase;

/**
 * native code to watch each other when api under 20 (contains 20)
 *
 * @author Mars
 */
public class NativeDaemonAPI20 extends NativeDaemonBase {

    static {
        try {
            System.loadLibrary("daemon_api20");
        } catch (Exception e) {
            Log.e("", "", e);
        }
    }

    public NativeDaemonAPI20(Context context) {
        super(context);
    }

    public native void doDaemon(String pkgName, String svcName, String daemonPath);

}
