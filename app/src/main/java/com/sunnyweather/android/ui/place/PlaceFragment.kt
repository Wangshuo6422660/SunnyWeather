package com.sunnyweather.android.ui.place

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.databinding.FragmentPlaceBinding
import com.sunnyweather.android.ui.weather.WeatherActivity


// 由于搜索城市数据的功能在后面还会复用，因此不建议写在Activity里面，而是应该写在Fragment里面
// 这样当需要复用时，直接在布局里面引入该Fragment即可
class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    private lateinit var binding : FragmentPlaceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_place, container, false)
        binding = FragmentPlaceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 对存储的状态进行判断、读取
        // 现在已将PlaceFragment嵌入WeatherActivity中之后，如果还直接跳转到WeatherActivity肯定是不行的，因为这会造成无限循环跳转的情况
            // 需要先对PlaceFragment所在的活动进行判断——只有当PlaceFragment被嵌入MainActivity中，并且之前已经存在选中的城市
            // 才会直接跳转到WeatherActivity，这样就会解决无限循环跳转的问题了
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        binding.recyclerView.adapter = adapter
        // 监听搜索框内容的变化情况
        binding.searchPlaceEdit.addTextChangedListener { editable ->
            val content = editable.toString()
            // 每当搜索框中的内容发生了变化，就获取新的内容，然后传递给PlaceViewModel的searchPlaces()方法——搜索城市数据的网络请求
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {         // 当输入搜索框中的内容为空时，就将RecyclerView隐藏起来，同时将那张仅用于美观用途的背景图显示出来
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        // 获取服务器响应的数据——数据有任何变化，就会回调到传入的Observer接口中实现
        viewModel.placeLiveData.observe(viewLifecycleOwner) { result ->
            val places = result.getOrNull()
            if (places != null) {
                binding.recyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }

    }

}