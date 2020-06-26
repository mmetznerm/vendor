package br.com.mmetzner.vendor.admin.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Client
import kotlinx.android.synthetic.main.activity_orders_item.view.*

class SelectClientAdapter(private var mItems: List<Client?>, private val listener: OnClickListener) : RecyclerView.Adapter<SelectClientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectClientViewHolder {
        return SelectClientViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_select_client_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SelectClientViewHolder, position: Int) {
        val item = mItems[position]

        holder.itemView.tvClient.text = item?.name
        holder.itemView.setOnClickListener { listener.onItemClicked(position, item) }
    }

    interface OnClickListener {
        fun onItemClicked(position: Int, client: Client?)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateItems(items: List<Client?>) {
        this.mItems = items
        notifyDataSetChanged()
    }

}