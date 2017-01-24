package com.marswin89.marsdaemon;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Mars
 */
public class DaemonClient implements IDaemonClient {
    private static final String TAG = "DaemonClient";

    private static final String DAEMON_PERMITTING_SP_FILENAME = "d_permit";
    private static final String DAEMON_PERMITTING_SP_KEY = "permitted";
    private DaemonConfigurations mConfigurations;
    private BufferedReader mBufferedReader;//release later to save time

    public DaemonClient(DaemonConfigurations configurations) {
        this.mConfigurations = configurations;
    }

    public boolean setDaemonPermiiting(Context context, boolean isPermitting) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(DAEMON_PERMITTING_SP_KEY, isPermitting);
        return editor.commit();
    }

    @Override
    public void onAttachBaseContext(Context base) {
        initDaemon(base);
    }


    // spend too much time !! 60+ms
    // private String getProcessName() {
    //     ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
    //     int pid = android.os.Process.myPid();
    //     List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
    //     for (int i = 0; i < infos.size(); i++) {
    //         RunningAppProcessInfo info = infos.get(i);
    //         if (pid == info.pid) {
    //             return info.processName;
    //         }
    //     }
    //     return null;
    // }

    /**
     * do some thing about daemon
     *
     * @param base
     */
    private void initDaemon(Context base) {
        // if (!isDaemonPermitting(base) || mConfigurations == null) {
        if (mConfigurations == null) {
            Log.i(TAG, "initDaemon[false]");
            return;
        }
        Log.i(TAG, "initDaemon[true]");
        String processName = getProcessName();
        String packageName = base.getPackageName();

        if (null != processName) {
            if (processName.startsWith(mConfigurations.PERSISTENT_CONFIG.PROCESS_NAME)) {
                IDaemonStrategy.Fetcher.fetchStrategy().onPersistentCreate(base, mConfigurations);
            } else if (processName.startsWith(mConfigurations.DAEMON_ASSISTANT_CONFIG.PROCESS_NAME)) {
                IDaemonStrategy.Fetcher.fetchStrategy().onDaemonAssistantCreate(base, mConfigurations);
            } else if (processName.startsWith(packageName)) {
                IDaemonStrategy.Fetcher.fetchStrategy().onInitialization(base);
            }
        }

        releaseIO();
    }

    private String getProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            mBufferedReader = new BufferedReader(new FileReader(file));
            return mBufferedReader.readLine();
        } catch (Exception e) {
            Log.e("", "", e);
            return null;
        }
    }

    /**
     * release reader IO
     */
    private void releaseIO() {
        if (mBufferedReader != null) {
            try {
                mBufferedReader.close();
            } catch (IOException e) {
                Log.e("", "", e);
            }
            mBufferedReader = null;
        }
    }

    private boolean isDaemonPermitting(Context context) {
        SharedPreferences sp = context.getSharedPreferences(DAEMON_PERMITTING_SP_FILENAME, Context.MODE_PRIVATE);
        return sp.getBoolean(DAEMON_PERMITTING_SP_KEY, true);
    }

}
