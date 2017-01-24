package com.marswin89.marsdaemon.strategy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.marswin89.marsdaemon.DaemonConfigurations;
import com.marswin89.marsdaemon.IDaemonStrategy;
import com.marswin89.marsdaemon.nativ.NativeDaemonAPI21;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * the strategy in android API 23.
 * ---------------------------------27s----
 * os_version_name : 6.0.1
 * os_version_code : 23
 * os_display_name : MMB29M
 * brand_info : Xiaomi
 * product_info : cancro_wc_lte
 * model_info : MI 4LTE
 * manufacturer_info : Xiaomi
 * ----------------------------------107s----
 * os_version_name : 6.0.1
 * os_version_code : 23
 * os_display_name : MMB29K.G9250ZCU2DPG3
 * brand_info : samsung
 * product_info : zeroltezc
 * model_info : SM-G9250
 * manufacturer_info : samsung
 */
public class DaemonStrategy23 implements IDaemonStrategy {
    private static final int INTENT_FLAG = 23000;

    private final static String INDICATOR_DIR_NAME = "indicators";
    private final static String INDICATOR_PERSISTENT_FILENAME = "indicator_p";
    private final static String INDICATOR_DAEMON_ASSISTANT_FILENAME = "indicator_d";
    private final static String OBSERVER_PERSISTENT_FILENAME = "observer_p";
    private final static String OBSERVER_DAEMON_ASSISTANT_FILENAME = "observer_d";

    private IBinder mRemote;
    private Parcel mBroadcastData;
    private DaemonConfigurations mConfigs;

    @Override
    public boolean onInitialization(Context context) {
        return initIndicatorFiles(context);
    }

    @Override
    public void onPersistentCreate(final Context context, DaemonConfigurations configs) {
        ComponentName componentName = new ComponentName(context.getPackageName(), configs.PERSISTENT_CONFIG.SERVICE_NAME);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        intent.setAction(configs.INTENT_ACTION + "_" + INTENT_FLAG);
        // intent.setFlags(INTENT_FLAG);
        context.startService(intent);

        initAmsBinder();
        initBroadcastParcel(context, configs.DAEMON_ASSISTANT_CONFIG.RECEIVER_NAME, configs.INTENT_ACTION);
        sendBroadcastByAmsBinder();

        Thread t = new Thread() {
            public void run() {
                File indicatorDir = context.getDir(INDICATOR_DIR_NAME, Context.MODE_PRIVATE);
                new NativeDaemonAPI21(context).doDaemon(
                        new File(indicatorDir, INDICATOR_PERSISTENT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, INDICATOR_DAEMON_ASSISTANT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_PERSISTENT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_DAEMON_ASSISTANT_FILENAME).getAbsolutePath());
            }
        };
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();


        if (configs.LISTENER != null) {
            this.mConfigs = configs;
            configs.LISTENER.onPersistentStart(context);
        }
    }

    @Override
    public void onDaemonAssistantCreate(final Context context, DaemonConfigurations configs) {
        initAmsBinder();
        initBroadcastParcel(context, configs.PERSISTENT_CONFIG.RECEIVER_NAME, configs.INTENT_ACTION);
        sendBroadcastByAmsBinder();

        Thread t = new Thread() {
            public void run() {
                File indicatorDir = context.getDir(INDICATOR_DIR_NAME, Context.MODE_PRIVATE);
                new NativeDaemonAPI21(context).doDaemon(
                        new File(indicatorDir, INDICATOR_DAEMON_ASSISTANT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, INDICATOR_PERSISTENT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_DAEMON_ASSISTANT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_PERSISTENT_FILENAME).getAbsolutePath());
            }

            ;
        };
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();


        if (configs.LISTENER != null) {
            this.mConfigs = configs;
            configs.LISTENER.onDaemonAssistantStart(context);
        }
    }


    @Override
    public void onDaemonDead() {
        if (sendBroadcastByAmsBinder()) {
            if (mConfigs != null && mConfigs.LISTENER != null) {
                mConfigs.LISTENER.onWatchDaemonDaed();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }


    private void initAmsBinder() {
        Class<?> activityManagerNative;
        try {
            activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Object amn = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
            Field mRemoteField = amn.getClass().getDeclaredField("mRemote");
            mRemoteField.setAccessible(true);
            mRemote = (IBinder) mRemoteField.get(amn);
        } catch (Exception e) {
            Log.e("", "", e);
        }
    }


    @SuppressLint("Recycle")
    /** when process dead, we should save time to restart and kill self, don`t take a waste of time to recycle*/
    private void initBroadcastParcel(Context context, String broadcastName, String intentAction) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(context.getPackageName(), broadcastName);
        intent.setComponent(componentName);
        intent.setAction(intentAction + "_" + (INTENT_FLAG + 1));
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        //get ContextImpl instance
        // Object contextImpl = ((Application)context.getApplicationContext()).getBaseContext();
        //this context is ContextImpl, get MainThread instance immediately
        // Field mainThreadField = context.getClass().getDeclaredField("mMainThread");
        // mainThreadField.setAccessible(true);
        // Object mainThread = mainThreadField.get(context);
        // //get ApplicationThread instance
        // Object applicationThread = mainThread.getClass().getMethod("getApplicationThread").invoke(mainThread);
        // //get Binder
        // Binder callerBinder = (Binder) (applicationThread.getClass().getMethod("asBinder").invoke(applicationThread));

        //get handle
        // UserHandle userHandle = android.os.Process.myUserHandle();
        // int handle = (Integer) userHandle.getClass().getMethod("getIdentifier").invoke(userHandle);

        //write pacel
        mBroadcastData = Parcel.obtain();
        mBroadcastData.writeInterfaceToken("android.app.IActivityManager");
        // mBroadcastData.writeStrongBinder(callerBinder);
        mBroadcastData.writeStrongBinder(null);
        intent.writeToParcel(mBroadcastData, 0);
        mBroadcastData.writeString(intent.resolveTypeIfNeeded(context.getContentResolver()));
        mBroadcastData.writeStrongBinder(null);
        mBroadcastData.writeInt(Activity.RESULT_OK);
        mBroadcastData.writeString(null);
        mBroadcastData.writeBundle(null);
        mBroadcastData.writeString(null);
        mBroadcastData.writeInt(-1);
        mBroadcastData.writeInt(0);
        mBroadcastData.writeInt(0);
        // mBroadcastData.writeInt(handle);
        mBroadcastData.writeInt(0);
    }


    private boolean sendBroadcastByAmsBinder() {
        try {
            if (mRemote == null || mBroadcastData == null) {
                Log.e("Daemon", "REMOTE IS NULL or PARCEL IS NULL !!!");
                return false;
            }
            mRemote.transact(14, mBroadcastData, null, 0);//BROADCAST_INTENT_TRANSACTION = 0x00000001 + 13
            return true;
        } catch (RemoteException e) {
            Log.e("", "", e);
            return false;
        }
    }


    private boolean initIndicatorFiles(Context context) {
        File dirFile = context.getDir(INDICATOR_DIR_NAME, Context.MODE_PRIVATE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            createNewFile(dirFile, INDICATOR_PERSISTENT_FILENAME);
            createNewFile(dirFile, INDICATOR_DAEMON_ASSISTANT_FILENAME);
            return true;
        } catch (IOException e) {
            Log.e("", "", e);
            return false;
        }
    }

    private void createNewFile(File dirFile, String fileName) throws IOException {
        File file = new File(dirFile, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
