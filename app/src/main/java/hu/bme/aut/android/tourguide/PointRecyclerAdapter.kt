package hu.bme.aut.android.tourguide

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.route_point_row.view.*

class PointRecyclerAdapter constructor(val context: Context): RecyclerView.Adapter<PointRecyclerAdapter.PointHolder>() {

    var pointList = mutableListOf<Point>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointHolder {
        return  PointHolder(LayoutInflater.from(parent.context).inflate(R.layout.route_point_row, parent, false))
    }

    override fun getItemCount() = pointList.size

    override fun onBindViewHolder(holder: PointHolder, position: Int) {
        val point = pointList[position]
        holder.tvName.text = point.name
        holder.tvDescription.text = point.description

        holder.point = point
    }

    inner class PointHolder (pointView: View): RecyclerView.ViewHolder(pointView){
        val tvName = pointView.tv_route_point_name
        val tvDescription = pointView.tv_route_point_description

        var point : Point? = null

        init {
            pointView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("url", point!!.url)
                val fragment = WebViewFragment()
                fragment.arguments = bundle
                (context as NavigationActivity).replaceFragment(fragment)
            }
        }
    }
}