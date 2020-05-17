package hu.bme.aut.android.tourguide

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RoutesFragment : Fragment() {
    private val TAG = "RoutesFragment"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RouteRecyclerAdapter
    private lateinit var myFilter: MyFilter
    private var isFiltered = false
    private lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_routes, container, false)

        val user = arguments!!.getSerializable("user") as User

        fab = view.findViewById(R.id.fab_routes)
        fab.setOnClickListener {
            if(isFiltered){
                val simpleFilter = MyFilter(mutableListOf(), 0.0, 1000.0, 0.0, 300.0)
                showRoutes(simpleFilter)
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_purple_24dp))
            }else{
                val bundle = Bundle()
                bundle.putSerializable("user", user)
                val fragmentDial = ChooseFilterDialogFragment()
                fragmentDial.arguments = bundle
                fragmentDial.show(activity!!.supportFragmentManager, "TAG")
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_purple_24dp))
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

    private fun shouldShow(route: Route) : Boolean{
        var show = false
        Log.d(TAG, "${myFilter.minDist}<=${route.distance}<=${myFilter.maxDist}###${myFilter.minTime}<=${route.time}<=${myFilter.maxTime}###${route.name}")
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

    fun resetFabBackground(){
        fab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_purple_24dp))
        isFiltered = false
    }
}
