package com.sunnyweather.android.logic.dao

import android.content.Context
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.edit
import com.sunnyweather.android.logic.model.Place



// 记录选中的城市
object PlaceDao {

    // 存储数据
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            // 先通过GSON将Place对象转成一个JSON字符串，然后就可以用字符串存储的方式来保存数据了
            putString("place", Gson().toJson(place))
        }
    }

    // 读取数据
    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        // 通过GSON将JSON字符串解析成Place对象返回
        return Gson().fromJson(placeJson, Place::class.java)
    }

    // 判断是否有数据已被存储
    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunnyWeatherApplication.context.
        getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}