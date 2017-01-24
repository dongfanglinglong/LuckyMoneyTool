package com.marswin89.marsdaemon;

import android.app.Application;
import android.content.Context;

/**
 * make your Application extends it
 *
 * @author Mars
 */
public abstract class DaemonApplication extends Application {
    private IDaemonClient mDaemonClient;
    /**
     * order to prevent performing super.attachBaseContext() by child class</br>
     * if do it, it will cause the IllegalStateException if a base context has already been set.
     */
    private boolean mHasAttachBaseContext = false;

    public DaemonApplication() {
        mDaemonClient = new DaemonClient(getDaemonConfigurations());
    }

    /**
     * Daemon SDK needs the Daemon Configurations contains two process informations</br>
     * see {@link DaemonConfigurations} and {@link DaemonConfigurations.DaemonConfiguration}
     *
     * @return DaemonConfigurations
     */
    protected abstract DaemonConfigurations getDaemonConfigurations();

    @Override
    public final void attachBaseContext(Context base) {
        if (mHasAttachBaseContext) {
            return;
        }
        mHasAttachBaseContext = true;
        super.attachBaseContext(base);
        mDaemonClient.onAttachBaseContext(base);
        attachBaseContextByDaemon(base);
    }

    /**
     * instead of {{@link #attachBaseContext(Context)}, you can override this.</br>
     *
     * @param base
     */
    public void attachBaseContextByDaemon(Context base) {

    }


}
