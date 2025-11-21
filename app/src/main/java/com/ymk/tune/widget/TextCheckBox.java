package com.ymk.tune.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.ymk.tune.R;
import com.ymk.tune.databinding.ItemTextCheckboxBinding;

import java.util.Objects;

/**
 * 带文字的 CheckBox
 *
 * @author YouMingKun
 * @since 2024-01-19
 */
public final class TextCheckBox extends ConstraintLayout {

    private final ItemTextCheckboxBinding binding;

    public TextCheckBox(@NonNull Context context) {
        this(context, null);
    }

    public TextCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TextCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        String text;
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TextCheckBox);
            text = array.getString(R.styleable.TextCheckBox_tcbText);
            array.recycle();
        } else {
            text = "";
        }

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.item_text_checkbox, this, true);

        if (Objects.isNull(binding)) {
            return;
        }

        binding.itcText.setText(text);
        binding.getRoot().setOnClickListener(v -> {
            boolean checked = binding.itcIcon.isChecked();
            binding.itcIcon.setChecked(!checked, true);
        });
        binding.itcIcon.setOnCheckedChangeListener((checkBox, isChecked) -> {
            if (isChecked && Objects.nonNull(onCheckListener)) {
                onCheckListener.onCheck();
            }
        });
    }

    public void setChecked(boolean checked) {
        if (Objects.isNull(binding) || checked == binding.itcIcon.isChecked()) {
            return;
        }
        binding.itcIcon.setChecked(checked);
    }

    private OnCheckListener onCheckListener;

    public void setOnCheckListener(OnCheckListener listener) {
        onCheckListener = listener;
    }

    /**
     * 选中事件监听
     */
    public interface OnCheckListener {
        /**
         * 选中事件监听
         */
        void onCheck();
    }
}