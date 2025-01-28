package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine



// 统一的网络数据源访问入口，对所有网络请求的API进行封装

object SunnyWeatherNetwork {

    // 对PlaceService接口进行封装

    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()


    // 对WeatherService接口进行封装

    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealtimeWeather(lng, lat).await()

    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()


    // 不同的Service接口返回的数据类型不同——使用泛型
    // Call<T>的扩展函数、挂起函数
    private suspend fun <T> Call<T>.await(): T {
        // suspendCoroutine(必须在 协程作用域 或 挂起函数中调用)会挂起当前协程，然后在普通线程中执行Lambda表达式
        // 中的代码；Lambda表达式的参数列表上会传入一个Continuation参数，调用它的resume()或resumeWithException()
        // 可以让协程恢复执行
        return suspendCoroutine { continuation ->
            // 调用enqueue()方法时，Retrofit就会根据注解中配置的服务器接口地址去进行网络请求，服务器响应的数据会回
            // 调到enqueue()方法中传入的Callback实现里面。需要注意的是，当发起请求时，Retrofit会自动在内部开启子
            // 线程，当数据回调到Callback中之后，Retrofit又会自动切换回主线程，整个操作过程中无需考虑线程切换问题
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T?>, response: Response<T?>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }
                override fun onFailure(call: Call<T?>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

}