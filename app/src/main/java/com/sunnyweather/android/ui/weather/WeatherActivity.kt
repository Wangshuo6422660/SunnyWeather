package com.sunnyweather.android.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale



// 请求天气数据，并将数据展示到界面上

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }

//    private lateinit var binding : ActivityWeatherBinding

    private lateinit var navBtn : Button
    lateinit var drawerLayout : DrawerLayout
    private lateinit var swipeRefresh : SwipeRefreshLayout
    private lateinit var placeName : TextView
    private lateinit var currentTemp : TextView
    private lateinit var currentSky : TextView
    private lateinit var currentAQI : TextView
    private lateinit var nowLayout : RelativeLayout
    private lateinit var forecastLayout : LinearLayout
    private lateinit var coldRiskText : TextView
    private lateinit var dressingText : TextView
    private lateinit var ultravioletText : TextView
    private lateinit var carWashingText : TextView
    private lateinit var weatherLayout : ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
//        binding = ActivityWeatherBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        drawerLayout = findViewById(R.id.drawerLayout)
        navBtn = findViewById(R.id.navBtn)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        placeName = findViewById(R.id.placeName)
        currentTemp = findViewById(R.id.currentTemp)
        currentSky = findViewById(R.id.currentSky)
        currentAQI = findViewById(R.id.currentAQI)
        nowLayout = findViewById(R.id.nowLayout)
        forecastLayout = findViewById(R.id.forecastLayout)
        coldRiskText = findViewById(R.id.coldRiskText)
        dressingText = findViewById(R.id.dressingText)
        ultravioletText = findViewById(R.id.ultravioletText)
        carWashingText = findViewById(R.id.carWashingText)
        weatherLayout = findViewById(R.id.weatherLayout)

        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                // 解析与展示 服务器返回的天气
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            // 请求结束之后，需要将SwipeRefreshLayout 的isRefreshing 属性设置成false——表示刷新事件结束，并隐藏刷新进度条
            swipeRefresh.isRefreshing = false
        }
//        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)

        // 设置下拉刷新进度条的颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }

        // 将背景图与状态栏融合到一起
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT

        // 加入滑动菜单的逻辑处理
        navBtn.setOnClickListener {              // 在切换城市按钮的点击事件中调用DrawerLayout的openDrawer()方法来打开滑动菜单
            drawerLayout.openDrawer(GravityCompat.START)
        }
        // 监听DrawerLayout的状态，当滑动菜单被隐藏的同时，隐藏输入法——在滑动菜单中搜索城市时会弹出输入法，如果滑动菜单隐藏后输入法
        // 却还显示在界面上，就会是一种非常怪异的情况
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        // 填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        // 在未来几天天气预报的部分，使用for-in循环来处理每天的天气信息，在循环中动态
        // 加载forecast_item.xml布局并设置相应的布局
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            // 设置日期信息
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            // 设置天气图标、信息
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            // 设置温度
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            // 添加相应布局中的视图
            forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        // 生活指数方面虽然服务器会返回很多天的数据，但界面上只需要当天(下标为零)的数据就行
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        // 设置完所有数据之后，让ScrollView变成可见状态
        weatherLayout.visibility = View.VISIBLE
    }
}