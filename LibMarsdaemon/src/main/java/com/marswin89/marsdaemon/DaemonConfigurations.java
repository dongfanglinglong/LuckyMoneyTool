package com.marswin89.marsdaemon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * the configurations of Daemon SDK, contains two process configuration.
 *
 * @author Mars
 */
public class DaemonConfigurations {

    public final DaemonConfiguration PERSISTENT_CONFIG;
    public final DaemonConfiguration DAEMON_ASSISTANT_CONFIG;
    public final DaemonListener LISTENER;
    public final String INTENT_ACTION;


    public DaemonConfigurations(@NonNull DaemonConfiguration persistentConfig,
                                @NonNull DaemonConfiguration daemonAssistantConfig) {
        this.PERSISTENT_CONFIG = persistentConfig;
        this.DAEMON_ASSISTANT_CONFIG = daemonAssistantConfig;
        this.INTENT_ACTION = null;
        this.LISTENER = null;
    }

    public DaemonConfigurations(@NonNull DaemonConfiguration persistentConfig,
                                @NonNull DaemonConfiguration daemonAssistantConfig,
                                @NonNull String action) {
        this.PERSISTENT_CONFIG = persistentConfig;
        this.DAEMON_ASSISTANT_CONFIG = daemonAssistantConfig;
        this.INTENT_ACTION = action;
        this.LISTENER = null;
    }

    public DaemonConfigurations(@NonNull DaemonConfiguration persistentConfig,
                                @NonNull DaemonConfiguration daemonAssistantConfig,
                                @NonNull String action,
                                @Nullable DaemonListener listener) {
        this.PERSISTENT_CONFIG = persistentConfig;
        this.DAEMON_ASSISTANT_CONFIG = daemonAssistantConfig;
        this.INTENT_ACTION = action;
        this.LISTENER = listener;
    }


    /**
     * listener of daemon for external
     *
     * @author Mars
     */
    public interface DaemonListener {
        void onPersistentStart(Context context);

        void onDaemonAssistantStart(Context context);

        void onWatchDaemonDaed();
    }

    /**
     * the configuration of a daemon process, contains process name, service name and receiver name if Android 6.0
     *
     * @author guoyang
     */
    public static class DaemonConfiguration {

        public final String PROCESS_NAME;
        public final String SERVICE_NAME;
        public final String RECEIVER_NAME;

        public DaemonConfiguration(String processName, String serviceName, String receiverName) {
            this.PROCESS_NAME = processName;
            this.SERVICE_NAME = serviceName;
            this.RECEIVER_NAME = receiverName;
        }
    }
}
