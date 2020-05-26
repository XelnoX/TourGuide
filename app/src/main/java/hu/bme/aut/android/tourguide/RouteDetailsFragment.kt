package hu.bme.aut.android.tourguide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RouteDetailsFragment : Fragment() {

    private lateinit var route: Route
    private lateinit var tvName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvBack: TextView
    private lateinit var btnSelect: Button
    private lateinit var tvCity: TextView
    //private lateinit var navView: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PointRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_route_details, container, false)

        //navView = view.findViewById(R.id.bottom_navigation_view)
        recyclerView = view.findViewById(R.id.rv_route_detail_points)
        adapter = PointRecyclerAdapter(context!!)

        route = arguments!!.getSerializable("route") as Route

        adapter.pointList = route.points
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        tvName = view.findViewById(R.id.tv_route_details_name)
        tvDescription = view.findViewById(R.id.tv_route_details_description)
        tvDistance = view.findViewById(R.id.tv_route_details_distance)
        tvTime = view.findViewById(R.id.tv_route_details_time)
        tvBack = view.findViewById(R.id.tv_route_details_back)
        btnSelect = view.findViewById(R.id.btn_route_details_select)
        tvCity = view.findViewById(R.id.tv_route_details_city)

        tvName.text = route.name
        tvDescription.text = route.description
        val distTemp = "Total distance: ${route.distance} km"
        tvDistance.text = distTemp
        val timeTemp = "Estimated time: ${route.time} perc"
        tvTime.text =  timeTemp
        val cityTemp = "The route takes place in the city of ${route.city}"
        tvCity.text = cityTemp

        tvBack.setOnClickListener {
            fragmentManager!!.popBackStack()
        }

        btnSelect.setOnClickListener {
            //navView.menu.findItem(R.id.navigation_map).isChecked = true
            val fragment = MapFragment()
            fragment.pointList = route.points
            (activity as NavigationActivity).replaceFragment(fragment)
        }

        return view
    }

}
