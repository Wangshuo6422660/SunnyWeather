package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context



class SunnyWeatherApplication: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        // 彩云App申请到的令牌值(方便之后的获取)
        const val TOKEN = "y1y7RKSpsRVpdfvl"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}