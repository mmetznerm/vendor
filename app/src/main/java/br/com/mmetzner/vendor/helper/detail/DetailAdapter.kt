package br.com.mmetzner.vendor.helper.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Charge
import kotlinx.android.synthetic.main.activity_detail_item.view.*

class DetailAdapter(private var mItems: List<Charge>, private val listener: OnClickListener) : RecyclerView.Adapter<DetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_detail_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = mItems[position]

        holder.itemView.tvDate.text = item.date
        holder.itemView.tvValue.text = item.value.toString()
        holder.itemView.setOnClickListener {
            item.selected = !item.selected
            listener.onItemClicked(position, item)
            notifyItemChanged(position)
        }
        if(item.selected) {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_background_green)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_background_blue)
        }
    }

    interface OnClickListener {
        fun onItemClicked(position: Int, charge: Charge)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateItems(items: List<Charge>) {
        this.mItems = items
        notifyDataSetChanged()
    }

    fun getItems(): List<Charge> {
        return mItems
    }

}