package com.ymk.volume;

import android.Manifest;
import android.media.AudioManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

/**
 * 音量工具类
 * <p>
 * 参考 <a href="https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/src/main/java/com/blankj/utilcode/util/VolumeUtils.java">AndroidUtilCode - VolumeUtils</a>
 *
 * @author YMK
 * @since 2025/11/21
 */
final class VolumeUtils {

    private VolumeUtils() {
        throw new UnsupportedOperationException("u can't initialize me~");
    }

    /**
     * 根据 streamType 获取音量
     */
    public static int getVolume(@NonNull AudioManager manager, int streamType) {
        return manager.getStreamVolume(streamType);
    }

    /**
     * 设置音量到指定 streamType
     *
     * @param volume 当 volume 超过最大值时，不会抛出异常，而是最大化音量。低于最小值时同理
     * @param flags  参考：
     *               <ul>
     *               <li>{@link AudioManager#FLAG_SHOW_UI}</li>
     *               <li>{@link AudioManager#FLAG_ALLOW_RINGER_MODES}</li>
     *               <li>{@link AudioManager#FLAG_PLAY_SOUND}</li>
     *               <li>{@link AudioManager#FLAG_REMOVE_SOUND_AND_VIBRATE}</li>
     *               <li>{@link AudioManager#FLAG_VIBRATE}</li>
     *               </ul>
     */
    public static void setVolume(@NonNull AudioManager manager, int streamType, int volume, int flags) {
        try {
            manager.setStreamVolume(streamType, volume, flags);
        } catch (SecurityException ignore) {
            // CODE HERE
        }
    }

    /**
     * 返回指定 streamType 的最大值
     */
    public static int getMaxVolume(@NonNull AudioManager manager, int streamType) {
        return manager.getStreamMaxVolume(streamType);
    }

    /**
     * 返回指定 streamType 的最小值
     */
    public static int getMinVolume(@NonNull AudioManager manager, int streamType) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? manager.getStreamMinVolume(streamType) : 0;
    }

    /**
     * 获取当前系统响铃模式
     */
    public static int getRingMode(@NonNull AudioManager manager) {
        return manager.getRingerMode();
    }

    /**
     * 设置系统响铃模式
     */
    @RequiresPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
    public static void setRingMode(@NonNull AudioManager manager, int ringMode) {
        try {
            manager.setRingerMode(ringMode);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断指定 streamType 是否静音
     */
    public static boolean isMute(@NonNull AudioManager manager, int streamType) {
        return manager.isStreamMute(streamType);
    }

    /**
     * 设置指定 streamType 静音状态
     */
    public static void setMute(@NonNull AudioManager manager, int streamType, boolean isMute, int flag) {
        manager.adjustStreamVolume(
                streamType,
                isMute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE,
                flag);
    }

    /**
     * 获取 streamType 的名称
     */
    public static String getStreamTypeName(final int streamType) {
        switch (streamType) {
            case AudioManager.STREAM_MUSIC:
                return "STREAM_MUSIC";
            case AudioManager.STREAM_ALARM:
                return "STREAM_ALARM";
            case AudioManager.STREAM_ACCESSIBILITY:
                return "STREAM_ACCESSIBILITY";
            case AudioManager.STREAM_SYSTEM:
                return "STREAM_SYSTEM";
            case AudioManager.STREAM_DTMF:
                return "STREAM_DTMF";
            case AudioManager.STREAM_NOTIFICATION:
                return "STREAM_NOTIFICATION";
            case AudioManager.STREAM_RING:
                return "STREAM_RING";
            case AudioManager.STREAM_VOICE_CALL:
                return "STREAM_VOICE_CALL";
            case AudioManager.USE_DEFAULT_STREAM_TYPE:
                return "USE_DEFAULT_STREAM_TYPE";
            default:
                return String.valueOf(streamType);
        }
    }
}