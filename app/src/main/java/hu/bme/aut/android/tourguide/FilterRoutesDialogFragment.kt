package hu.bme.aut.android.tourguide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FilterRoutesDialogFragment : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CityRecyclerAdapter
    private lateinit var btnShow: Button
    private lateinit var etMinDist: EditText
    private lateinit var etMaxDist: EditText
    private lateinit var etMinTime: EditText
    private lateinit var etMaxTime: EditText

    private lateinit var routesFrag: RoutesFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter_routes, container, false)

        btnShow = view.findViewById(R.id.btn_filter_show_routes)
        etMinDist = view.findViewById(R.id.et_filter_min_dist)
        etMaxDist = view.findViewById(R.id.et_filter_max_dist)
        etMinTime = view.findViewById(R.id.et_filter_min_time)
        etMaxTime = view.findViewById(R.id.et_filter_max_time)
        recyclerView = view.findViewById(R.id.rv_filter_routes)

        adapter = CityRecyclerAdapter()
        val list = (activity as NavigationActivity).cityList.toMutableList()

        adapter.cityList = list

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        btnShow.setOnClickListener {
            routesFrag = fragmentManager!!.findFragmentById(R.id.fragment_holder) as RoutesFragment
            val cityNameList = mutableListOf<String>()
            for(city in list){
                if(city.isSelected){
                    cityNameList.add(city.name)
                }
            }
            var minDist = etMinDist.text.toString()
            if(minDist.isEmpty()){
                minDist = 0.0.toString()
            }
            var maxDist = etMaxDist.text.toString()
            if(maxDist.isEmpty()){
                maxDist = 1000.0.toString()
            }
            var minTime = etMinTime.text.toString()
            if(minTime.isEmpty()){
                minTime = 0.0.toString()
            }
            var maxTime = etMaxTime.text.toString()
            if(maxTime.isEmpty()){
                maxTime = 300.0.toString()
            }
            val filter = MyFilter(cityNameList, minDist.toDouble(), maxDist.toDouble(), minTime.toDouble(), maxTime.toDouble())
            routesFrag.showRoutes(filter)
            dismiss()
        }
        return  view
    }
}
