package com.df.moneytool.services;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Rect;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.df.moneytool.Gloable;
import com.df.moneytool.R;
import com.df.moneytool.utils.PreferenceUtil;
import com.df.moneytool.utils.ULog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongfang on 2016-01-25
 */
public class MoneyToolService extends AccessibilityService {

    // ---------- 红包涉及关键字 --------------
    // 出现红包
    private final static String WECHAT_NOTIFICATION_TIP = "[微信红包]";

    // 领取红包
    private static final String WECHAT_VIEW_SELF_CH = "查看红包";
    private static final String WECHAT_VIEW_OTHERS_CH = "领取红包";

    // 关闭红包页面
    private static final String WECHAT_DETAILS_EN = "Details";
    private static final String WECHAT_DETAILS_CH = "红包详情";
    private static final String WECHAT_BETTER_LUCK_EN = "Better luck next time!";
    private static final String WECHAT_BETTER_LUCK_CH = "手慢了";


    // ---------- 抢红包操作状态 --------------
    /** 通知栏收到红包 */
    private static final int LUCKYMONEY_NOTIFICATION = 0x1000; // 通知栏收到红包
    /** listview显示红包 */
    private static final int LUCKYMONEY_SHOW = 0x0100; // listview显示红包
    /** 点开红包页面 */
    private static final int LUCKYMONEY_OPEN = 0x0010; // 点开红包页面
    /** 抢红包 */
    private static final int LUCKYMONEY_GET = 0x0001; // 抢红包
    /** 关闭红包页面 */
    private static final int LUCKYMONEY_CLOSE = 0x0000; // 关闭红包页面

    // ---------- Handler --------------
    //    /** 检测到是红包，但是没有触发打开红包页面，用Handler进行红包数量递减逻辑 */
    //    private static final int CANCEL_GET_LUCKYMONEY_TIME = 1000 * 1;
    //    /** 减少红包数量 */
    //    private static final int HANDLER_ID_REDUCE_LUCKYMONEY_NUM = 1000;


    /** 自动抢红包状态 */
    private int luckyMoneyStatus = 0x0000;

    private AccessibilityNodeInfo rootNodeInfo;
    /** 群对话框中的listview */
    private AccessibilityNodeInfo lucMoneyListView;

    /** 红包数量 */
    private int luckyMoneyNum = 0;


    /**
     * AccessibilityEvent的回调方法
     *
     * @param event 事件
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        ULog.i(event.toString());

        if (AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED != event.getEventType() && null == (rootNodeInfo = event.getSource())) {
            ULog.e("null == (rootNodeInfo = event.getSource())");
            return;
        }

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                ULog.i("----------------TYPE_NOTIFICATION_STATE_CHANGED-------------");
                if (!Gloable.MONITOR_NOTIFICATION)
                    return;

                if (event.getText().toString().contains(WECHAT_NOTIFICATION_TIP)) {
                    Parcelable parcelable = event.getParcelableData();
                    if (parcelable instanceof Notification) {
                        Notification notification = (Notification) parcelable;
                        try {
                            notification.contentIntent.send();
                        }
                        catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED: {
                if (!Gloable.MONITOR_SCROLLED) return;

                lucMoneyListView = rootNodeInfo;
                chkLuckyMoneyAndOpenOnce();
            }
            break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                if (!Gloable.MONITOR_OPEN_LUCKYMONEY) return;

                String className = event.getClassName().toString();
                ULog.d("className = " + className);
                if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className)) {
                    // 抢到红包的详情页
                    if (LUCKYMONEY_OPEN == (LUCKYMONEY_OPEN & luckyMoneyStatus)) {
                        performGlobalAction(GLOBAL_ACTION_BACK);
                    }
                    luckyMoneyStatus = LUCKYMONEY_CLOSE;
                }
                else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(className)) {
                    getLuckyMoney();
                }
                else if ("com.tencent.mm.ui.LauncherUI".equals(className)) {
                    luckyMoneyStatus = LUCKYMONEY_CLOSE;
                }

                if (LUCKYMONEY_CLOSE == luckyMoneyStatus || LUCKYMONEY_GET == (LUCKYMONEY_GET & luckyMoneyStatus)) {
                    if (luckyMoneyNum > 0) {
                        luckyMoneyNum = luckyMoneyNum > 0 ? luckyMoneyNum-- : 0;
                        ULog.e("findLuckMoneyAndOpen again luckyMoneyNum = " + luckyMoneyNum);
                        findLuckMoneyAndOpen();
                    }
                }
            }
            break;
        }

    }


    /** 检测是否有红包进入列表,如果有则打开红包 */
    private boolean chkLuckyMoneyAndOpenOnce() {
        if (null == lucMoneyListView)
            return false;
        ULog.d("rootNodeInfo.getChildCount() = " + lucMoneyListView.getChildCount() + ",luckyMoneyNum = " + luckyMoneyNum);
        int index = lucMoneyListView.getChildCount() > 0 ? lucMoneyListView.getChildCount() - 1 : 0;
        ULog.d("index = " + index);

        if (null != lucMoneyListView.getChild(index)) {
            List<AccessibilityNodeInfo> list = findAccessibilityNodeInfosByTexts(
                    lucMoneyListView.getChild(index),
                    new String[]{
                            WECHAT_VIEW_OTHERS_CH,
                            WECHAT_VIEW_SELF_CH
                    }
            );

            if (!list.isEmpty() && isLuckyMoney(list.get(0))) {
                luckyMoneyStatus |= LUCKYMONEY_SHOW;
                if (LUCKYMONEY_OPEN != (luckyMoneyStatus & LUCKYMONEY_OPEN)) {
                    list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    luckyMoneyStatus |= LUCKYMONEY_OPEN;
                    return true;
                }

                luckyMoneyNum++;
                ULog.d("luckyMoneyNum = " + luckyMoneyNum);
            }
            // else{
            //     //当聊天对话判定不是红包时，所有红包逻辑结束
            //     luckyMoneyNum = 0;
            // }
        }
        return false;
    }

    /** 多个红包是需要触发 */
    private boolean findLuckMoneyAndOpen() {
        if (null == lucMoneyListView)
            return false;
        ULog.d("rootNodeInfo.getChildCount() = " + lucMoneyListView.getChildCount() + ",luckyMoneyNum = " + luckyMoneyNum);
        int index = lucMoneyListView.getChildCount() > 0 ? lucMoneyListView.getChildCount() - 1 - luckyMoneyNum : 0;
        index = index < 0 ? 0 : index;
        ULog.d("index = " + index);

        if (null != lucMoneyListView.getChild(index)) {
            List<AccessibilityNodeInfo> list = findAccessibilityNodeInfosByTexts(
                    lucMoneyListView.getChild(index),
                    new String[]{
                            WECHAT_VIEW_OTHERS_CH,
                            WECHAT_VIEW_SELF_CH
                    }
            );

            if (!list.isEmpty() && LUCKYMONEY_OPEN != (luckyMoneyStatus & LUCKYMONEY_OPEN)) {
                luckyMoneyStatus |= LUCKYMONEY_SHOW;
                list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                luckyMoneyStatus |= LUCKYMONEY_OPEN;
                return true;
            }
        }
        return false;
    }


    /** 抢红包 ,如果红包已经抢完，则关闭红包 */
    private void getLuckyMoney() {
        /* 戳开红包，红包还没抢完，遍历节点匹配“拆红包” */
        List<AccessibilityNodeInfo> list = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b2c");
        if (null == list || list.isEmpty()) {
            list = rootNodeInfo.findAccessibilityNodeInfosByText("拆红包");
        }

        if (null != list && !list.isEmpty()) {
            list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            luckyMoneyStatus |= LUCKYMONEY_GET;
            return;
        }

        // 自动打开的红包才会关闭
        if (LUCKYMONEY_OPEN == (LUCKYMONEY_OPEN & luckyMoneyStatus)) {
            list = findAccessibilityNodeInfosByTexts(
                    rootNodeInfo,
                    new String[]{WECHAT_BETTER_LUCK_CH,
                            WECHAT_DETAILS_CH,
                            WECHAT_BETTER_LUCK_EN,
                            WECHAT_DETAILS_EN
                    }
            );

            if (!list.isEmpty()) {
                performGlobalAction(GLOBAL_ACTION_BACK);
                luckyMoneyStatus = LUCKYMONEY_CLOSE;
            }
        }

    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, R.string.app_started, Toast.LENGTH_SHORT).show();
        PreferenceUtil.init(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
    }


    /**
     * 批量化执行AccessibilityNodeInfo.findAccessibilityNodeInfosByText(text).
     * 由于这个操作影响性能,将所有需要匹配的文字一起处理,尽早返回
     *
     * @param nodeInfo 窗口根节点
     * @param texts    需要匹配的字符串们
     * @return 匹配到的节点数组
     */
    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String[] texts) {
        for (String text : texts) {
            if (text == null) continue;

            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);

            if (!nodes.isEmpty()) return nodes;
        }
        return new ArrayList<>();
    }

    private boolean isLuckyMoney(AccessibilityNodeInfo info) {
        Rect rect = new Rect();
        info.getBoundsInParent(rect);
        ULog.d(rect.toString());

        return 212 == rect.right && 66 == rect.bottom;

    }


}
