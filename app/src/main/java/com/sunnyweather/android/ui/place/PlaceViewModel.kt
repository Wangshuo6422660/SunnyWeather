package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place



// ViewModel层，相当于逻辑层和UI层之间的一个桥梁

class PlaceViewModel: ViewModel() {

    private val searchLiveData = MutableLiveData<String>()

    // 用于对界面上显示的城市数据进行缓存——原则上，与界面相关的数据都应该放到ViewModel中
    // 这样可以保证它们在手机屏幕发生旋转时不会丢失
    val placeList = ArrayList<Place>()

    // 每当searchPlaces()函数被调用时，switchMap()方法所对应的转换函数就会执行
    // 然后在转换函数中，只需要调用仓库层中定义的searchPlaces()方法就可以发起网络请求，
    // 同时将仓库层返回的LiveData对象转换成一个可供Activity观察的LiveData对象
    val placeLiveData = searchLiveData.switchMap { query ->
        Repository.searchPlaces(query)
    }

    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}