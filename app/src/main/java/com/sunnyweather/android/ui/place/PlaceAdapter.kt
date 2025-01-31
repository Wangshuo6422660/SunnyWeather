package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.weather.WeatherActivity



// RecyclerView为子项(城市数据)布局的列表控件，其需要一个适配器

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>):
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
//        return ViewHolder(view)
        // 给place_item的最外层布局注册了一个点击事件监听器，然后在点击事件监听器中获取当前点击项的经纬度坐标、地区名称
        // 并将它们传入Intent中，最后调用Fragment的startActivity()方法启动WeatherActivity
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val place = placeList[position]
            // 处理切换城市之后的逻辑
            // 此工作必须在PlaceAdapter中进行，因为之前选中了某个城市后是跳转到WeatherActivity的，
            // 而现在由于我们本来就是在WeatherActivity中的，因此不需要跳转，只需去请求新选择城市的天气信息就行
            val activity = fragment.activity
            if (activity is WeatherActivity) {
                // 如果在WeatherActivity中，那么就关闭滑动菜单，给WeatherViewModel赋值新的经纬度坐标、地区名称，然后刷新城市的天气信息
                activity.drawerLayout.closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            } else {
                // 如果是在MainActivity中，保持之前的处理逻辑即可
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)
                }
                fragment.startActivity(intent)
                activity?.finish()
            }
            // 在跳转到WeatherActivity之前，先调用PlaceViewModel的savePlace()方法来存储选中的城市
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount() = placeList.size
}