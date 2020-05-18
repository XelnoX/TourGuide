package hu.bme.aut.android.tourguide

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CitiesDialogFragment : DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CityRecyclerAdapter
    private lateinit var from: String
    private lateinit var profFrag: ProfileFragment
    private lateinit var btnOk: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cities_dialog, container, false)
        from = arguments!!.getString("from")!!
        btnOk = view.findViewById(R.id.btn_cities_ok)
        btnCancel = view.findViewById(R.id.btn_cities_cancel)

        recyclerView = view.findViewById(R.id.rv_reg_cities)
        adapter = CityRecyclerAdapter()

        if(from == "RegA"){
            adapter.cityList = (activity as RegistrationActivity).cityList
            adapter.notifyDataSetChanged()
        }else if(from == "ProfF"){
            adapter.cityList = (activity as NavigationActivity).getList()
            adapter.notifyDataSetChanged()
            profFrag = fragmentManager!!.findFragmentById(R.id.fragment_holder) as ProfileFragment
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        btnOk.setOnClickListener {
            if(from == "RegA"){
                (activity as RegistrationActivity).fillTexView()
            }else if(from == "ProfF"){
                profFrag.fillTextView()
            }
            dismiss()
        }
        btnCancel.setOnClickListener {
            if(from == "ProfF"){
                (activity as NavigationActivity).resetCityList()
            }
            dismiss()
        }
        return view
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if(from == "ProfF"){
            (activity as NavigationActivity).resetCityList()
        }
    }
}
