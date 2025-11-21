package com.ymk.volume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;

/**
 * 音量变化监听
 *
 * @author YMK
 * @since 2025/11/20
 */
final class VolumeChangeObserver extends BroadcastReceiver {

    /**
     * 是否初始化
     */
    private boolean isInit = false;

    /**
     * 音量改变广播 Action
     * <p>
     * 参考 {@link AudioManager}
     * <p>
     * 系统属性无法调用 故此处定义一个相同值的变量
     */
    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";

    /**
     * 静音状态改变广播
     * <p>
     * 参考 {@link AudioManager}
     */
    private static final String STREAM_MUTE_CHANGED_ACTION = "android.media.STREAM_MUTE_CHANGED_ACTION";

    /**
     * 用于在收到广播时，获取改变的 STREAM
     * <p>
     * 参考 {@link AudioManager}
     */
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    /**
     * 用于在收到广播时，获取改变 之后 的具体数值
     * <p>
     * 参考 {@link AudioManager}
     */
    private static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
    /**
     * 用于在收到广播时，获取改变 之前 的具体数值
     * <p>
     * 参考 {@link AudioManager}
     */
    private static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";

    /**
     * 用于在收到广播时，获取 STREAM 的静音状态
     * <p>
     * 参考 {@link AudioManager}
     */
    private static final String EXTRA_STREAM_VOLUME_MUTED = "android.media.EXTRA_STREAM_VOLUME_MUTED";

    /// ////////////////////////////////////////////////////////////////////////////////////////////
    /// 单例
    /// ////////////////////////////////////////////////////////////////////////////////////////////

    private VolumeChangeObserver() {
        // CODE HERE
    }

    /**
     * 静态内部类实现单例
     */
    private static class SingleHolder {
        private static final VolumeChangeObserver INSTANCE = new VolumeChangeObserver();
    }

    /**
     * 单例模式
     */
    public static VolumeChangeObserver getInstance() {
        return SingleHolder.INSTANCE;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////
    /// 开放接口
    /// ////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == intent) {
            return;
        }

        String action = intent.getAction();

        // 音量流改变
        if (VOLUME_CHANGED_ACTION.equals(action)) {
            int streamType = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
            int value = intent.getIntExtra(EXTRA_VOLUME_STREAM_VALUE, -1);
            int preValue = intent.getIntExtra(EXTRA_PREV_VOLUME_STREAM_VALUE, -1);
            Volume.getInstance().onVolumeChanged(streamType, value, preValue);
            return;
        }

        // 静音事件
        if (STREAM_MUTE_CHANGED_ACTION.equals(action)) {
            int streamType = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1);
            boolean isMute = intent.getBooleanExtra(EXTRA_STREAM_VOLUME_MUTED, false);
            Volume.getInstance().onMuteChanged(streamType, isMute);
            return;
        }

        // 响铃模式改变
        if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(action)) {
            int ringMode = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1);
            Volume.getInstance().onRingModeChanged(ringMode);
        }
    }

    /**
     * 注册监听
     */
    public void init(Context context) {
        if (isInit || null == context) {
            throw new UnsupportedOperationException("init fail: reInit or context is null");
        }

        IntentFilter filter = new IntentFilter();
        // 音量改变监听
        filter.addAction(VOLUME_CHANGED_ACTION);
        // 静音监听
        // 通过系统设置静音时 不会发送音量改变广播 需要监听静音广播进行处理
        filter.addAction(STREAM_MUTE_CHANGED_ACTION);
        // 响铃模式改变监听
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        context.registerReceiver(this, filter);

        isInit = true;
    }

    /**
     * 取消注册
     */
    public void exit(Context context) {
        if (!isInit) {
            return;
        }
        if (null == context) {
            throw new UnsupportedOperationException("exit fail: context is null");
        }

        context.unregisterReceiver(this);
        isInit = false;
    }
}