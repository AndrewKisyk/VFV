package com.wrapper.composechat

import android.app.Application
import com.wrapper.composechat.di.initKoinAndroid

class VfvMultiplatformApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid(this)
    }
}
