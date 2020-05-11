package hu.bme.aut.android.tourguide

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.util.logging.Filter

class RoutesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RouteRecyclerAdapter
    private lateinit var myFilter: MyFilter
    private var isFiltered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_routes, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab_routes)
        fab.setOnClickListener {
            if(isFiltered){
                val simpleFilter = MyFilter(mutableListOf<String>(), 0.0, 1000.0, 0.0, 300.0)
                showRoutes(simpleFilter)
                fab.background = resources.getDrawable(R.drawable.ic_search_purple_24dp)
            }else{
                val fragmentDial = ChooseFilterDialogFragment()
                fragmentDial.show(activity!!.supportFragmentManager, "TAG")
                //fab.background = resources.getDrawable(R.drawable.ic_c)
            }
            isFiltered = !isFiltered
        }

        recyclerView = view.findViewById(R.id.rv_routes)
        adapter = RouteRecyclerAdapter(activity as NavigationActivity)

        adapter.routeList = (activity as NavigationActivity).routeList

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        return view
    }

    fun showRoutes(filter: MyFilter){
        myFilter = filter
        val newRouteList = mutableListOf<Route>()
        for(route in (activity as NavigationActivity).routeList){
            if(shouldShow(route)){
                newRouteList.add(route)
            }
        }
        adapter.routeList = newRouteList
        adapter.notifyDataSetChanged()
    }

    fun shouldShow(route: Route) : Boolean{
        var show = false
        Log.d("lel", "${myFilter.minDist}<=${route.distance}<=${myFilter.maxDist}###${myFilter.minTime}<=${route.time}<=${myFilter.maxTime}###${route.name}")
        if(route.distance >= myFilter.minDist && route.distance <= myFilter.maxDist && route.time >= myFilter.minTime && route.time <= myFilter.maxTime){
            if(myFilter.cityNameList.size != 0){
                for(name in myFilter.cityNameList){
                    if(route.city == name){
                        show = true
                    }
                }
            }else{
                show = true
            }

        }
        return show
    }
}
