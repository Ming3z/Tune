package com.ymk.tune.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;

import com.ymk.tune.R;
import com.ymk.tune.databinding.ItemVolumeProgressBinding;
import com.ymk.volume.Volume;

import java.util.Objects;

/**
 * 音量进度条
 *
 * @author YouMingKun
 * @since 2024-01-18
 */
public final class VolumeProgress extends LinearLayout implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "VolumeProgress";

    private final int streamType;
    private final int minVolume;

    private final ItemVolumeProgressBinding binding;

    public VolumeProgress(Context context) {
        this(context, null);
    }

    public VolumeProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public VolumeProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        String title;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VolumeProgress);
            title = array.getString(R.styleable.VolumeProgress_vpTitle);
            streamType = array.getInt(R.styleable.VolumeProgress_vpType, -1);
            array.recycle();
        } else {
            title = "";
            streamType = -1;
        }

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.item_volume_progress, this, true);
        if (Objects.nonNull(binding)) {
            binding.setState(this);
            binding.ivpTitle.setText(title);
            binding.ivpProgress.setMax(Volume.getInstance().getMaxVolume(streamType));
            binding.ivpProgress.setOnSeekBarChangeListener(this);
        }
        minVolume = Volume.getInstance().getMinVolume(streamType);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 状态
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 当前进度
     */
    public final ObservableInt progressLiveData = new ObservableInt(0);

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 开放接口
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 设置进度
     */
    public void setProgress(int progress) {
        if (isTracking) {
            return;
        }
        this.progressLiveData.set(progress);
    }

    /**
     * {@link SeekBar.OnSeekBarChangeListener}
     */

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i(TAG + "onProgressChanged",
                "progress = " + progress + "; fromUser = " + fromUser);

        // 音量最小值限制
        if (progress < minVolume) {
            binding.ivpProgress.setProgress(minVolume);
            return;
        }

        // 不是用户拖动改变音量 直接返回 防止重复设置导致视图抖动
        if (!fromUser) {
            return;
        }

        // 拖动时改变状态文字
        if (Objects.nonNull(binding)) {
            binding.ivpProgressText.setText(String.valueOf(progress));
        }
        // 设置音量
        Volume.getInstance().setVolume(streamType, progress, 0);
    }

    /**
     * 是否正在滑动
     * <p>
     * 主要作用是禁止滑动时外部设置 progress 状态 防止视图抖动
     */
    private boolean isTracking = false;

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isTracking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        isTracking = false;
    }
}