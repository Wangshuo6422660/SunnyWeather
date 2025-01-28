package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query



// 访问(彩云天气)城市搜索API的Retrofit接口

interface PlaceService {

    @GET("v2/place?&token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    // Retrofit将服务器返回的JSON数据自动解析成PlaceResponse对象
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>

}