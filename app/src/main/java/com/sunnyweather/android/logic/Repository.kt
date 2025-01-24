package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers



// 仓库层的统一封装入口
// 每次都发起网络请求去获取最新的数据
// 一般在仓库层中定义的方法，为了能将异步获取的数据以响应式编程的方式通知给上一层，通常会返回一个LiveData对象

object Repository {

    // liveData()函数可以自动构建并返回一个LiveData对象，然后在它的代码块中提供一个挂起函数的上下文，
    // 这样就可以在liveData()函数的代码块中调用任意的挂起函数
    // Android不允许在主线程中进行网络请求、读写数据库之类的本地数据操作
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        // 发送包装的结果，此方法类似于调用LiveData的setValue()方法来通知数据变化，
        // 此处无法直接获取返回的LiveData对象
        emit(result)
    }
}