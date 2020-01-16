package com.df.moneytool;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.df.moneytool.utils.PreferenceUtil;

/**
 * Created by dongfang on 2016-01-25.
 */
public class SettingsActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    /** 通知栏 */
    private CheckBox chkNotification;
    /** 对话列表 */
    private CheckBox chkListView;
    /** 打开红包 */
    private CheckBox chkOpen;
    /** 保存红包记录 */
    private CheckBox chkSaveLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        chkNotification = findViewById(R.id.checkBox_notification);
        chkListView = findViewById(R.id.checkBox_listview);
        chkOpen = findViewById(R.id.checkbox_open_luckyMoney);
        chkSaveLog = findViewById(R.id.checkBox_logger);


        chkNotification.setOnCheckedChangeListener(this);
        chkListView.setOnCheckedChangeListener(this);
        chkOpen.setOnCheckedChangeListener(this);
        chkSaveLog.setOnCheckedChangeListener(this);

        findViewById(R.id.preference_back).setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        chkNotification.setChecked(Gloable.MONITOR_NOTIFICATION);
        chkListView.setChecked(Gloable.MONITOR_SCROLLED);
        chkOpen.setChecked(Gloable.MONITOR_OPEN_LUCKYMONEY);
        chkSaveLog.setChecked(Gloable.MONITOR_SAVE_LOGGER);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.preference_back:
            this.finish();
            break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        switch (v.getId()) {
        case R.id.checkBox_notification:
            PreferenceUtil.setMonitorNotification(this, isChecked);
            Gloable.MONITOR_NOTIFICATION = isChecked;
            break;
        case R.id.checkBox_listview:
            PreferenceUtil.setMonitorScrolled(this, isChecked);
            Gloable.MONITOR_SCROLLED = isChecked;

            break;
        case R.id.checkbox_open_luckyMoney:
            PreferenceUtil.setMonitorOpenLuckymoney(this, isChecked);
            Gloable.MONITOR_OPEN_LUCKYMONEY = isChecked;

            break;
        case R.id.checkBox_logger:
            PreferenceUtil.setMonitorSaveLogger(this, isChecked);
            Gloable.MONITOR_SAVE_LOGGER = isChecked;

            break;
        }
    }
}
