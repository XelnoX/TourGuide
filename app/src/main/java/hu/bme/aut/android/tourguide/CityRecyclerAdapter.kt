package hu.bme.aut.android.tourguide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.city_row.view.*

class CityRecyclerAdapter: RecyclerView.Adapter<CityRecyclerAdapter.CityHolder>() {

    var cityList = mutableListOf<City>()

    inner class CityHolder (cityView : View): RecyclerView.ViewHolder(cityView){
        val tvName = cityView.tv_city_row
        val cbSelected = cityView.cb_city_row

        var city : City? = null

        init {
            cbSelected.setOnCheckedChangeListener { _, isChecked ->
                city!!.isSelected = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityHolder {
        return CityHolder(LayoutInflater.from(parent.context).inflate(R.layout.city_row, parent, false))
    }

    override fun getItemCount() = cityList.size

    override fun onBindViewHolder(holder: CityHolder, position: Int) {
        val city = cityList[position]
        holder.city = city
        holder.tvName.text = city.name
        holder.cbSelected.isChecked = city.isSelected
    }
}