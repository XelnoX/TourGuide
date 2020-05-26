package hu.bme.aut.android.tourguide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.route_row.view.*

class RouteRecyclerAdapter (val activity: NavigationActivity) : RecyclerView.Adapter<RouteRecyclerAdapter.RouteHolder>() {

    var routeList = mutableListOf<Route>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteHolder {
        return  RouteHolder(LayoutInflater.from(parent.context).inflate(R.layout.route_row, parent, false))
    }

    override fun getItemCount() = routeList.size

    override fun onBindViewHolder(holder: RouteHolder, position: Int) {
        val route = routeList[position]
        holder.route = route
        holder.tvName.text = route.name
        holder.tvDescription.text = route.description
        holder.tvCityName.text = route.city
    }

    inner class RouteHolder(routeView: View) : RecyclerView.ViewHolder(routeView) {
        val tvName: TextView = routeView.tv_route_name
        val tvDescription: TextView = routeView.tv_route_description
        val tvCityName: TextView = routeView.tv_route_city_name

        var route: Route? = null

        init {
            routeView.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable("route", route)
                val fragment = RouteDetailsFragment()
                fragment.arguments = bundle
                activity.addToBackStackAndReplaceFragment(fragment)

            }
        }
    }
}