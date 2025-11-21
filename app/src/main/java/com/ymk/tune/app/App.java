package com.ymk.tune.app;

import com.ymk.base.base.BaseApp;
import com.ymk.volume.Volume;

/**
 * @author YMK
 * @since 2025/11/21
 */
public class App extends BaseApp {

    @Override
    protected void initSdk() {
        super.initSdk();
        Volume.getInstance().init(this);
    }
}