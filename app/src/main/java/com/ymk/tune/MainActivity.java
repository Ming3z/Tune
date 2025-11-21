package com.ymk.tune;

import android.os.Bundle;

import com.ymk.base.base.BaseVmActivity;
import com.ymk.tune.databinding.ActivityMainBinding;

public final class MainActivity extends BaseVmActivity<ActivityMainBinding, MainVm> {

    @Override
    protected int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setVm(viewModel);
    }
}