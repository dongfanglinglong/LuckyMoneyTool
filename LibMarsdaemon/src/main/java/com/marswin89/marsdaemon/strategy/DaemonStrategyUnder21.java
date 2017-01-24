package com.marswin89.marsdaemon.strategy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;

import com.marswin89.marsdaemon.DaemonConfigurations;
import com.marswin89.marsdaemon.IDaemonStrategy;
import com.marswin89.marsdaemon.nativ.NativeDaemonAPI20;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * the strategy in android API below 21.
 * -------------------------------------------
 * os_version_name : 4.4.2
 * os_version_code : 19
 * os_display_name : P6-C00V100R001C92B709
 * brand_info : Huawei
 * product_info : P6-C00
 * model_info : HUAWEI P6-C00
 * manufacturer_info : HUAWEI
 * -------------------------------------------
 * os_version_name : 4.3
 * os_version_code : 18
 * os_display_name : R7007_11_150331
 * brand_info : OPPO
 * product_info : R7007
 * model_info : R7007
 * manufacturer_info : OPPO
 */
public class DaemonStrategyUnder21 implements IDaemonStrategy {
    private static final int INTENT_FLAG = 20000;

    private final String BINARY_DEST_DIR_NAME = "bin";
    private final String BINARY_FILE_NAME = "daemon";

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    @Override
    public boolean onInitialization(Context context) {
        return installBinary(context);
    }

    @Override
    public void onPersistentCreate(final Context context, final DaemonConfigurations configs) {
        initAlarm(context, configs.DAEMON_ASSISTANT_CONFIG.SERVICE_NAME, configs.INTENT_ACTION);
        Thread t = new Thread() {
            public void run() {
                File binaryFile = new File(context.getDir(BINARY_DEST_DIR_NAME, Context.MODE_PRIVATE), BINARY_FILE_NAME);
                new NativeDaemonAPI20(context).doDaemon(
                        context.getPackageName(),
                        configs.DAEMON_ASSISTANT_CONFIG.SERVICE_NAME,
                        binaryFile.getAbsolutePath());
            }
        };
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        if (configs.LISTENER != null) {
            configs.LISTENER.onPersistentStart(context);
        }
    }

    @Override
    public void onDaemonAssistantCreate(Context context, DaemonConfigurations configs) {
        Intent intent = new Intent();
        ComponentName component = new ComponentName(context.getPackageName(), configs.PERSISTENT_CONFIG.SERVICE_NAME);
        intent.setComponent(component);
        intent.setAction(configs.INTENT_ACTION + "_" + INTENT_FLAG);
        // intent.setFlags(INTENT_FLAG);
        context.startService(intent);
        if (configs.LISTENER != null) {
            configs.LISTENER.onWatchDaemonDaed();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    public void onDaemonDead() {
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 100, mPendingIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    private void initAlarm(Context context, String serviceName, String intentAction) {
        if (mAlarmManager == null) {
            mAlarmManager = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        }
        if (mPendingIntent == null) {
            Intent intent = new Intent();
            ComponentName component = new ComponentName(context.getPackageName(), serviceName);
            intent.setComponent(component);
            intent.setAction(intentAction + "_" + (INTENT_FLAG + 1));
            intent.setFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
            mPendingIntent = PendingIntent.getService(context, 0, intent, 0);
        }
        mAlarmManager.cancel(mPendingIntent);
    }


    private boolean installBinary(Context context) {
        String binaryDirName = null;
        String abi = Build.CPU_ABI;
        if (abi.startsWith("armeabi-v7a")) {
            binaryDirName = "armeabi-v7a";
        } else if (abi.startsWith("x86")) {
            binaryDirName = "x86";
        } else {
            binaryDirName = "armeabi";
        }
        return install(context, BINARY_DEST_DIR_NAME, binaryDirName, BINARY_FILE_NAME);
    }


    private boolean install(Context context, String destDirName, String assetsDirName, String filename) {
        File file = new File(context.getDir(destDirName, Context.MODE_PRIVATE), filename);
        if (file.exists()) {
            return true;
        }
        try {
            copyAssets(context, (TextUtils.isEmpty(assetsDirName) ? "" : (assetsDirName + File.separator)) + filename, file, "700");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void copyAssets(Context context, String assetsFilename, File file, String mode) throws IOException, InterruptedException {
        AssetManager manager = context.getAssets();
        final InputStream is = manager.open(assetsFilename);
        copyFile(file, is, mode);
    }

    private void copyFile(File file, InputStream is, String mode) throws IOException, InterruptedException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final String abspath = file.getAbsolutePath();
        final FileOutputStream out = new FileOutputStream(file);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        is.close();
        Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
    }
}
