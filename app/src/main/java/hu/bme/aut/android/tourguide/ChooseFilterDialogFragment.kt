package hu.bme.aut.android.tourguide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class ChooseFilterDialogFragment : DialogFragment() {

    private lateinit var btnData: Button
    private lateinit var btnNew: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_filter_dialog, container, false)

        btnData = view.findViewById(R.id.btn_choose_data)
        btnNew = view.findViewById(R.id.btn_choose_new)

        btnData.setOnClickListener {
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

}
