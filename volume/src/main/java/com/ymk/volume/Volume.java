package com.ymk.volume;

import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YMK
 * @since 2025/11/19
 */
public class Volume {

    private Context mContext;

    private AudioManager mAudioManager;

    /// ////////////////////////////////////////////////////////////////////////////////////////////
    /// 单例
    /// ////////////////////////////////////////////////////////////////////////////////////////////

    private Volume() {
        // CODE HERE
    }

    /**
     * 静态内部类实现单例
     */
    private static class SingleHolder {
        private static final Volume INSTANCE = new Volume();
    }

    /**
     * 单例模式
     */
    public static Volume getInstance() {
        return SingleHolder.INSTANCE;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////
    /// 状态
    /// ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 音量工具类 Map 初始化大小，根据音量 STREAM 设置
     */
    private static final int MAP_INIT_SIZE = 15;

    /**
     * 音量状态管理
     * <p>
     * 集合结构：Map< streamType, stateLiveData >
     */
    private ConcurrentHashMap<Integer, MutableLiveData<Integer>> volumeStateMap;

    /**
     * 系统响铃模式
     */
    private MutableLiveData<Integer> ringModeState;

    /**
     * 静音状态管理
     * <p>
     * 集合结构：Map< streamType, muteState >
     */
    private ConcurrentHashMap<Integer, MutableLiveData<Boolean>> muteStateMap;

    /// ////////////////////////////////////////////////////////////////////////////////////////////
    /// 事件
    /// ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 音量改变
     */
    void onVolumeChanged(final int streamType, final int value, final int preValue) {
        ConcurrentHashMap<Integer, MutableLiveData<Integer>> stateMap = getStateMap();
        MutableLiveData<Integer> stateLd = stateMap.get(streamType);
        if (null == stateLd) {
            return;
        }
        stateLd.postValue(value);
    }

    /**
     * 静音状态改变
     */
    void onMuteChanged(final int streamType, boolean isMute) {
        ConcurrentHashMap<Integer, MutableLiveData<Boolean>> muteMap = getMuteStateMap();
        MutableLiveData<Boolean> muteLv = muteMap.get(streamType);
        if (null != muteLv) {
            muteLv.postValue(isMute);
        }
    }

    /**
     * 响铃模式改变
     */
    void onRingModeChanged(final int ringMode) {
        if (null == ringModeState) {
            return;
        }
        ringModeState.postValue(ringMode);
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////
    /// 方法
    /// ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取 AudioManager
     */
    private AudioManager audioMgr() {
        if (null == mContext) {
            throw new UnsupportedOperationException("u need to initialize first~");
        }
        if (null == mAudioManager) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        return mAudioManager;
    }

    /**
     * 获取存储音量状态的 Map
     */
    private ConcurrentHashMap<Integer, MutableLiveData<Integer>> getStateMap() {
        if (null == volumeStateMap) {
            volumeStateMap = new ConcurrentHashMap<>(MAP_INIT_SIZE);
        }
        return volumeStateMap;
    }

    /**
     * 获取存储响铃状态的 MutableLiveData
     */
    private MutableLiveData<Integer> getRingModeLv() {
        if (null == ringModeState) {
            ringModeState = new MutableLiveData<>(VolumeUtils.getRingMode(audioMgr()));
        }
        return ringModeState;
    }

    /**
     * 获取存储静音状态的 Map
     */
    private ConcurrentHashMap<Integer, MutableLiveData<Boolean>> getMuteStateMap() {
        if (null == muteStateMap) {
            muteStateMap = new ConcurrentHashMap<>(MAP_INIT_SIZE);
        }
        return muteStateMap;
    }

    /// ////////////////////////////////////////////////////////////////////////////////////////////
    /// 开放接口
    /// ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 初始化
     * <p>
     * 在这一步会注册系统音量改变监听
     */
    public void init(Context context) {
        this.mContext = context;

        // 注册音量改变监听
        VolumeChangeObserver.getInstance().init(mContext);
    }

    /**
     * 逆初始化
     */
    public void exit() {

        // 注销音量改变监听
        VolumeChangeObserver.getInstance().exit(mContext);

        this.mContext = null;
        this.mAudioManager = null;
    }

    /**
     * 注册
     * <p>
     * 工具类内部根据生命周期，自行完成 {@link #init(Context)} 和 {@link #exit()} 方法
     *
     * @deprecated 只适合在单个 Activity 中使用的情况
     */
    @Deprecated
    public void register(@NonNull AppCompatActivity activity) {
        init(activity);
        activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                exit();
            }
        });
    }

    /**
     * 获取音量状态监听
     */
    @NonNull
    public LiveData<Integer> stateOf(int streamType) {
        ConcurrentHashMap<Integer, MutableLiveData<Integer>> stateMap = getStateMap();
        MutableLiveData<Integer> stateLv = stateMap.get(streamType);
        if (null == stateLv) {
            stateLv = new MutableLiveData<>(VolumeUtils.getVolume(audioMgr(), streamType));
            stateMap.put(streamType, stateLv);
        }
        return stateLv;
    }

    /**
     * 根据 streamType 获取当前音量
     * <p>
     * 默认返回 LiveData 中的音量状态
     * 如果 LiveData 没有被监听初始化，则查询系统音量
     */
    public int getVolume(int streamType) {
        MutableLiveData<Integer> stateLv = getStateMap().get(streamType);
        if (null == stateLv || null == stateLv.getValue()) {
            return VolumeUtils.getVolume(audioMgr(), streamType);
        } else {
            return stateLv.getValue();
        }
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
    public void setVolume(int streamType, int volume, int flags) {
        VolumeUtils.setVolume(audioMgr(), streamType, volume, flags);
    }

    /**
     * 模拟按键调节音量
     *
     * @param flag 参考：
     *             <ul>
     *             <li>{@link AudioManager#FLAG_SHOW_UI}</li>
     *             <li>{@link AudioManager#FLAG_ALLOW_RINGER_MODES}</li>
     *             <li>{@link AudioManager#FLAG_PLAY_SOUND}</li>
     *             <li>{@link AudioManager#FLAG_REMOVE_SOUND_AND_VIBRATE}</li>
     *             <li>{@link AudioManager#FLAG_VIBRATE}</li>
     *             </ul>
     */
    public void adjustVolume(int streamType, boolean isAdd, int flag) {
        try {
            audioMgr().adjustStreamVolume(streamType, isAdd ? AudioManager.ADJUST_RAISE : AudioManager.ADJUST_LOWER, flag);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟按键调节音量
     */
    public void adjustVolume(int streamType, boolean isAdd) {
        adjustVolume(streamType, isAdd, 0);
    }

    /**
     * 返回指定 streamType 的最大值
     */
    public int getMaxVolume(int streamType) {
        return VolumeUtils.getMaxVolume(audioMgr(), streamType);
    }

    /**
     * 返回指定 streamType 的最小值
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public int getMinVolume(int streamType) {
        return VolumeUtils.getMinVolume(audioMgr(), streamType);
    }

    /**
     * 获取系统响铃模式状态监听
     */
    public LiveData<Integer> ringMode() {
        return getRingModeLv();
    }

    /**
     * 获取当前系统响铃模式
     */
    public int getRingMode() {
        if (null == ringModeState || null == ringModeState.getValue()) {
            return VolumeUtils.getRingMode(audioMgr());
        } else {
            return ringModeState.getValue();
        }
    }

    /**
     * 设置系统响铃模式
     */
    @RequiresPermission(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
    public void setRingMode(int ringMode) {
        VolumeUtils.setRingMode(audioMgr(), ringMode);
    }

    /**
     * 是否是静音模式
     */
    public boolean isSilentMode() {
        return AudioManager.RINGER_MODE_SILENT == getRingMode();
    }

    /**
     * 是否是振动模式
     */
    public boolean isVibrateMode() {
        return AudioManager.RINGER_MODE_VIBRATE == getRingMode();
    }

    /**
     * 是否是响铃模式
     */
    public boolean isNormalMode() {
        return AudioManager.RINGER_MODE_NORMAL == getRingMode();
    }

    /**
     * 获取静音状态监听
     */
    public LiveData<Boolean> muteState(int streamType) {
        ConcurrentHashMap<Integer, MutableLiveData<Boolean>> muteMap = getMuteStateMap();
        MutableLiveData<Boolean> muteLv = muteMap.get(streamType);
        if (null == muteLv) {
            muteLv = new MutableLiveData<>(VolumeUtils.isMute(audioMgr(), streamType));
            muteMap.put(streamType, muteLv);
        }
        return muteLv;
    }

    /**
     * 获取某个音量流是否是静音状态
     *
     * <p>
     * 默认返回 LiveData 中的状态
     * 如果 LiveData 没有被监听初始化，则查询系统静音状态
     */
    public boolean isMute(int streamType) {
        MutableLiveData<Boolean> muteLv = getMuteStateMap().get(streamType);
        if (null == muteLv || null == muteLv.getValue()) {
            return VolumeUtils.isMute(audioMgr(), streamType);
        } else {
            return Boolean.TRUE.equals(muteLv.getValue());
        }
    }

    /**
     * 设置静音
     */
    public void setMute(int streamType, boolean mute) {
        VolumeUtils.setMute(audioMgr(), streamType, mute, 0);
    }

    /**
     * 根据当前静音状态 设置静音
     */
    public void setMute(int streamType) {
        setMute(streamType, !isMute(streamType));
    }
}