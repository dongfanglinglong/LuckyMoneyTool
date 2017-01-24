package com.df.moneytool;

import android.app.Application;
import android.content.Context;

import com.df.moneytool.daemon.Receiver4Base;
import com.df.moneytool.daemon.Receiver4Daemon;
import com.df.moneytool.daemon.Service4Daemon;
import com.df.moneytool.services.MoneyToolService;
import com.df.moneytool.utils.ULog;
import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;

/**
 * @author dongfang
 * @date 2017/1/24
 */

public class LuckyMoneyApplication extends Application {

    private DaemonClient mDaemonClient;

    private boolean mHasAttachBaseContext = false;

    @Override
    protected final void attachBaseContext(Context base) {
        if (mHasAttachBaseContext) {
            return;
        }
        mHasAttachBaseContext = true;
        super.attachBaseContext(base);
        mDaemonClient = new DaemonClient(createDaemonConfigurations());
        mDaemonClient.onAttachBaseContext(base);
    }

    /**
     * 守护进程配置
     *
     * @return
     */
    private DaemonConfigurations createDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.df.moneytool:pedometer",
                MoneyToolService.class.getCanonicalName(),
                Receiver4Base.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                Service4Daemon.PROCESS,
                Service4Daemon.class.getCanonicalName(),
                Receiver4Daemon.class.getCanonicalName());
        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, "daemon_intent_action_rent_Five", listener);
    }

    /**
     * 进程被杀异常情况
     */
    class MyDaemonListener implements DaemonConfigurations.DaemonListener {
        @Override
        public void onPersistentStart(Context context) {
            ULog.e();
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
            ULog.e();
        }

        @Override
        public void onWatchDaemonDaed() {
            ULog.e();
        }
    }
}
