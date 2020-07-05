package br.com.mmetzner.vendor.admin.neworder.truck

import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Truck
import br.com.mmetzner.vendor.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.dialog_select_truck.*

class SelectTruckDialog() : DialogFragment(), SelectTruckAdapter.TruckListener {

    private val mTrucks by lazy { Gson().fromJson<List<Truck>>(arguments?.getString(Constants.TRUCKS), object : TypeToken<List<Truck>>() {}.type) }
    private val mAdapter by lazy { SelectTruckAdapter(mTrucks, this) }
    private var listener: ClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_select_truck, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureList()
    }

    override fun onResume() {
        super.onResume()
        configureDialog()
    }

    private fun configureDialog() {
        val window = dialog.window
        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.getSize(size)
        window?.setLayout((size.x * 0.99).toInt(), (size.x * 1.30).toInt())
        window?.setGravity(Gravity.CENTER)
    }

    private fun configureList() {
        mAdapter.updateItems(mTrucks)

        val layoutManager = LinearLayoutManager(activity)
        rvTrucks.layoutManager = layoutManager
        rvTrucks.adapter = mAdapter
        rvTrucks.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
    }

    override fun onItemClick(truck: Truck) {
        listener?.onItemClickListener(truck)
        dismiss()
    }

    interface ClickListener {
        fun onItemClickListener(truck: Truck)
    }

    fun setOnClickListener(listener: ClickListener) {
        this.listener = listener
    }

    companion object {
        fun newInstance(trucks: String): SelectTruckDialog {
            val fragment = SelectTruckDialog()
            val args = Bundle()
            args.putString(Constants.TRUCKS, trucks)
            fragment.arguments = args
            return fragment
        }
    }

}