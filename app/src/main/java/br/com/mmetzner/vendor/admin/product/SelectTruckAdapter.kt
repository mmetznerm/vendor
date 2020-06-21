package br.com.mmetzner.vendor.admin.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Truck
import kotlinx.android.synthetic.main.dialog_select_truck_item.view.*

class SelectTruckAdapter(private var mItems: List<Truck>, private val listener: TruckListener) : RecyclerView.Adapter<SelectTruckViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectTruckViewHolder {
        return SelectTruckViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dialog_select_truck_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SelectTruckViewHolder, position: Int) {
        val item = mItems[position]
        holder.itemView.tvLicensePlate.text = item.licensePlate
        holder.itemView.setOnClickListener { listener.onItemClick(mItems[position]) }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateItems(items: List<Truck>) {
        this.mItems = items
        notifyDataSetChanged()
    }

    fun getItems(): List<Truck> {
        return mItems
    }

    interface TruckListener {
        fun onItemClick(truck: Truck)
    }

}