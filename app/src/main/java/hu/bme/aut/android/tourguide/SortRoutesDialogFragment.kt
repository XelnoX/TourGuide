package hu.bme.aut.android.tourguide

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.DialogFragment

class SortRoutesDialogFragment : DialogFragment() {

    private lateinit var btnCancel: Button
    private lateinit var listView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sort_routes_dialog, container, false)

        btnCancel = view.findViewById(R.id.btn_sort_routes_cancel)
        listView = view.findViewById(R.id.lv_sort_routes)
        val listItems = listOf("Cities in abc order", "No sort", "Shortest time first", "Longest time first", "Shortest distance first", "Longest distance first")
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            (fragmentManager!!.findFragmentById(R.id.fragment_holder) as RoutesFragment).sortRoutes(listItems[position])
            dismiss()
        }
        btnCancel.setOnClickListener {
            (fragmentManager!!.findFragmentById(R.id.fragment_holder) as RoutesFragment).resetSortFabBackground()
            dismiss()
        }

        return view
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        (fragmentManager!!.findFragmentById(R.id.fragment_holder) as RoutesFragment).resetSortFabBackground()
    }

}
