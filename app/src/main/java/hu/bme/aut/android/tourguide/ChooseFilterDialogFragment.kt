package hu.bme.aut.android.tourguide

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class ChooseFilterDialogFragment : DialogFragment() {
    private val TAG = "ChooseFilterDialogFragment"

    private lateinit var btnData: Button
    private lateinit var btnNew: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_filter_dialog, container, false)

        val user = arguments!!.getSerializable("user") as User
        val cityStringList = mutableListOf<String>()

        btnData = view.findViewById(R.id.btn_choose_data)
        btnNew = view.findViewById(R.id.btn_choose_new)

        btnData.setOnClickListener {
            for(city in user.cities){
                cityStringList.add(city.name)
            }
            val filter = MyFilter(cityStringList,0.0, 1000.0, 0.0, 300.0)
            (fragmentManager!!.findFragmentById(R.id.fragment_holder) as RoutesFragment).filterRoutes(filter)
            Toast.makeText(context, "These routes fitting for you:", Toast.LENGTH_LONG).show()
            dismiss()
        }
        btnNew.setOnClickListener {
            val fragmentDial = FilterRoutesDialogFragment()
            fragmentDial.show(activity!!.supportFragmentManager, "TAG")
            dismiss()
        }

        return view
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        (fragmentManager!!.findFragmentById(R.id.fragment_holder) as RoutesFragment).resetFilterFabBackground()
    }
}
