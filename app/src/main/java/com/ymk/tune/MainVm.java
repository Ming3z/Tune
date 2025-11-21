package com.ymk.tune;

import android.media.AudioManager;

import androidx.lifecycle.LiveData;

import com.ymk.base.base.BaseViewModel;
import com.ymk.volume.Volume;

/**
 * @author YMK
 * @since 2025/11/21
 */
public final class MainVm extends BaseViewModel {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 状态
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 响铃模式
     */
    public final LiveData<Integer> ringModeLvData = Volume.getInstance().ringMode();

    /**
     * 通话音量
     */
    public final LiveData<Integer> voiceCallLvData = Volume.getInstance().stateOf(AudioManager.STREAM_VOICE_CALL);

    public final LiveData<Integer> systemLvData = Volume.getInstance().stateOf(AudioManager.STREAM_SYSTEM);

    /**
     * 响铃、通知、系统默认音
     */
    public final LiveData<Integer> ringLvData = Volume.getInstance().stateOf(AudioManager.STREAM_RING);

    /**
     * 媒体音量
     */
    public final LiveData<Integer> musicLvData = Volume.getInstance().stateOf(AudioManager.STREAM_MUSIC);

    /**
     * 闹钟音量
     */
    public final LiveData<Integer> alarmLvData = Volume.getInstance().stateOf(AudioManager.STREAM_ALARM);

    public final LiveData<Integer> notificationLvData = Volume.getInstance().stateOf(AudioManager.STREAM_NOTIFICATION);

    public final LiveData<Integer> dtmfLvData = Volume.getInstance().stateOf(AudioManager.STREAM_DTMF);

    public final LiveData<Integer> accessibilityLvData = Volume.getInstance().stateOf(AudioManager.STREAM_ACCESSIBILITY);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 事件
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 设置响铃模式
     */
    private void setRingMode(int mode) {
        Volume.getInstance().setRingMode(mode);
    }

    /**
     * 设置响铃模式
     */
    public void setRingModeNormal() {
        setRingMode(AudioManager.RINGER_MODE_NORMAL);
    }

    /**
     * 设置振动模式
     */
    public void setRingModeVibrate() {
        setRingMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    /**
     * 设置静音模式
     */
    public void setRingModeSilent() {
        setRingMode(AudioManager.RINGER_MODE_SILENT);
    }
}