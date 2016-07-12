package com.df.moneytool.services;

/**
 * @author dongfang
 * @date 2016/7/12
 */
public class Const {


    // ---------- 红包涉及关键字 --------------
    // 出现红包
    public final static String WECHAT_NOTIFICATION_TIP = "[微信红包]";

    // 红包出现在listview时，判断红包出现的关键字和ID
    public static final String VIEW_LUCKYMONEY_SELF_NAME = "查看红包";
    public static final String VIEW_LUCKYMONEY_OTHERS_NAME = "领取红包";
    public static final String VIEW_OPEN_LUCKYMONEY_ITEM_ID = "com.tencent.mm:id/a1c";


    // 打开红包
    public static final String VIEW_GET_LUCKYMONEY_ID = "com.tencent.mm:id/b_b";

    // 关闭红包页面
    public static final String WECHAT_DETAILS_EN = "Details";
    public static final String WECHAT_DETAILS_CH = "红包详情";
    public static final String WECHAT_BETTER_LUCK_EN = "Better luck next time!";
    public static final String WECHAT_BETTER_LUCK_CH = "手慢了";

    // 红包详情页
    public static final String LUCKY_MONEY_DETAIL_ACTIVITY = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI";
    // 抢红包详情页
    public static final String LUCKY_MONEY_RECEIVE_ACTIVITY = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";
    // 微信聊天首页
    public static final String LUCKY_MONEY_LAUNCHER_ACTIVITY = "com.tencent.mm.ui.LauncherUI";

}
