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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RoutesFragment: Fragment() {
    private val TAG = "RoutesFragment"

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RouteRecyclerAdapter
    private var myFilter = MyFilter()
    private var mySortType = "No sort"
    private var isFiltered = false
    private var isSorted = false
    private lateinit var filterRoutesFab: FloatingActionButton
    private lateinit var sortRoutesFab: FloatingActionButton
    private var newRouteList = mutableListOf<Route>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_routes, container, false)

        recyclerView = view.findViewById(R.id.rv_routes)
        adapter = RouteRecyclerAdapter(activity as NavigationActivity)

        adapter.routeList = (activity as NavigationActivity).routeList

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        filterRoutesFab = view.findViewById(R.id.fab_filter_routes)
        sortRoutesFab = view.findViewById(R.id.fab_sort_routes)

        val user = arguments!!.getSerializable("user") as User

        if(isFiltered){
            filterRoutes(myFilter)
            filterRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_purple_24dp))
        }
        if(isSorted){
            sortRoutes(mySortType)
            sortRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_purple_24dp))
        }

        filterRoutesFab.setOnClickListener {
            if(isFiltered){
                isFiltered = !isFiltered
                sortRoutes(mySortType)
                myFilter = MyFilter()
                filterRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_purple_24dp))
            }else{
                isFiltered = !isFiltered
                val bundle = Bundle()
                bundle.putSerializable("user", user)
                val fragmentDial = ChooseFilterDialogFragment()
                fragmentDial.arguments = bundle
                fragmentDial.show(activity!!.supportFragmentManager, "TAG")
                filterRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_purple_24dp))
            }
        }

        sortRoutesFab.setOnClickListener{
            if(isSorted){
                isSorted = !isSorted
                filterRoutes(myFilter)
                mySortType = "No sort"
                sortRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_sort_purple_24dp))
            }else{
                isSorted = !isSorted
                val fragmentDial = SortRoutesDialogFragment()
                fragmentDial.show(activity!!.supportFragmentManager, "TAG")
                sortRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_clear_purple_24dp))
            }
        }

        return view
    }

    fun filterRoutes(filter: MyFilter){
        myFilter = filter
        val newRouteListForFilter = mutableListOf<Route>()
        val listForUse= if(isSorted){
            newRouteList
        }else{
            (activity as NavigationActivity).routeList
        }
        for(route in listForUse){
            if(shouldShow(route)){
                newRouteListForFilter.add(route)
            }
        }
        adapter.routeList = newRouteListForFilter
        adapter.notifyDataSetChanged()
        newRouteList = newRouteListForFilter
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

    fun resetFilterFabBackground(){
        filterRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_search_purple_24dp))
        isFiltered = false
    }

    fun sortRoutes(sortType: String){
        mySortType = sortType
        Log.d(TAG, sortType)
        val newRouteListForSort = mutableListOf<Route>()
        val listForUse = if(isFiltered){
            newRouteList
        }else{
            (activity as NavigationActivity).routeList
        }
        when (mySortType){
            "Cities in abc order"-> {
                val listOfCityNames = arrayListOf<String>()
                for(route in listForUse){
                    listOfCityNames.add(route.city)
                }
                listOfCityNames.sort()
                for(city in listOfCityNames){
                    Log.d(TAG, city)
                    for (route in listForUse){
                        if(route.city == city){
                            newRouteListForSort.add(route)
                        }
                    }
                }
                adapter.routeList = newRouteListForSort
            }
            "No sort"->{
                adapter.routeList = listForUse
            }
            "Shortest time first" -> {
                val listOfTimes = arrayListOf<Int>()
                for(route in listForUse){
                    listOfTimes.add(route.time)
                }
                listOfTimes.sort()
                for(time in listOfTimes){
                    Log.d(TAG, time.toString())
                    for (route in listForUse){
                        if(route.time == time){
                            newRouteListForSort.add(route)
                        }
                    }
                }
                adapter.routeList = newRouteListForSort
            }
            "Longest time first" -> {
                val listOfTimes = arrayListOf<Int>()
                for(route in listForUse){
                    listOfTimes.add(route.time)
                }
                listOfTimes.sort()
                val reversed = listOfTimes.asReversed()
                for(time in reversed){
                    Log.d(TAG, time.toString())
                    for (route in listForUse){
                        if(route.time == time){
                            newRouteListForSort.add(route)
                        }
                    }
                }
                adapter.routeList = newRouteListForSort
            }
            "Shortest distance first" -> {
                val listOfDistance = arrayListOf<Double>()
                for(route in listForUse){
                    listOfDistance.add(route.distance)
                }
                listOfDistance.sort()
                for(distance in listOfDistance){
                    Log.d(TAG, distance.toString())
                    for (route in listForUse){
                        if(route.distance == distance){
                            newRouteListForSort.add(route)
                        }
                    }
                }
                adapter.routeList = newRouteListForSort
            }
            "Longest distance first" -> {
                val listOfDistance = arrayListOf<Double>()
                for(route in listForUse){
                    listOfDistance.add(route.distance)
                }
                listOfDistance.sort()
                val reversed = listOfDistance.asReversed()
                for(distance in reversed ){
                    Log.d(TAG, distance.toString())
                    for (route in listForUse){
                        if(route.distance == distance){
                            newRouteListForSort.add(route)
                        }
                    }
                }
                adapter.routeList = newRouteListForSort
            }
        }
        adapter.notifyDataSetChanged()
        newRouteList = newRouteListForSort
    }

    fun resetSortFabBackground(){
        sortRoutesFab.setImageDrawable(resources.getDrawable(R.drawable.ic_sort_purple_24dp))
        isSorted = false
    }
}
