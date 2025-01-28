package com.sunnyweather.android.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Location



class WeatherViewModel: ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""

    var locationLat = ""

    var placeName = ""

    // 每当refreshWeather()函数被调用时，switchMap()方法所对应的转换函数就会执行
    // 然后在转换函数中，只需要调用仓库层中定义的refreshWeather()方法就可以发起网络请求，
    // 同时将仓库层返回的LiveData对象转换成一个可供Activity观察的LiveData对象
    val weatherLiveData = locationLiveData.switchMap { location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }
}