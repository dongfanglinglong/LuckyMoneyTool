package com.df.moneytool;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.df.moneytool.utils.ULog;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final Intent mAccessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private Button startServiceBtn;
    private TextView mVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main_new);
        startServiceBtn = findViewById(R.id.button_accessible);

        mVersion = findViewById(R.id.tv_version);
        mVersion.setText(getString(R.string.version, BuildConfig.VERSION_NAME));

        // handleMIUIStatusBar();
        // updateServiceStatus();
    }

//    /** 客户端版本 */
//    private String getAppVersion(Context context) {
//        try {
//            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            return pi.versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//            return "unknown";
//        }
//    }

    /**
     * 适配MIUI沉浸状态栏
     */
    private void handleMIUIStatusBar() {
        Window window = getWindow();

        Class clazz = window.getClass();
        try {
            int tranceFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);

            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, tranceFlag, tranceFlag);

            ImageView placeholder = (ImageView) findViewById(R.id.main_actiob_bar_placeholder);
            int placeholderHeight = getStatusBarHeight();
            placeholder.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, placeholderHeight));
        } catch (Exception e) {
            // Do nothing
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
    }


    /** 红包服务状态 */
    private void updateServiceStatus() {
        /** 服务是否开启 */
        boolean serviceEnabled = false;

        AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        String serviceName = getPackageName() + "/.services.MoneyToolService";
        ULog.d("serviceName = " + serviceName);

        for (AccessibilityServiceInfo info : accessibilityServices) {
            ULog.d("id  [" + info.getId() + "]");
            if (info.getId().equals(serviceName)) {
                serviceEnabled = true;
                break;
            }
        }

        startServiceBtn.setText(serviceEnabled ? R.string.service_off : R.string.service_on);
    }

    public void onButtonClicked(View view) {
        startActivity(mAccessibleIntent);
    }


    public void openGithub(View view) {
//        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_url)));
//        startActivity(i);
    }

    public void openSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return getResources().getDimensionPixelSize(resourceId);
        return 0;
    }


}
