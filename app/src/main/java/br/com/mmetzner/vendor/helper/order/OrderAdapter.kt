package br.com.mmetzner.vendor.helper.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView

import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Order
import br.com.mmetzner.vendor.model.Product
import kotlinx.android.synthetic.main.activity_orders_item.view.*

class OrderAdapter(private var mItems: List<Order?>?, private val listener: OnClickListener) : RecyclerView.Adapter<OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        return OrderViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_orders_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = mItems?.get(position)

        holder.itemView.tvClient.text = item?.client?.name
        holder.itemView.tvProduct.text = mountProductsDescription(item?.productList)
        holder.itemView.setOnClickListener { listener.onItemClicked(position, item) }

        if(item?.finished == true) {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_background_green)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_background_blue)
        }
    }

    private fun mountProductsDescription(productList: List<Product?>?): String {
        val builder = StringBuilder()
        productList?.sortedBy { it?.description }?.groupBy { it?.description }?.forEach { product ->
            builder.append("${product.value.sumBy { it?.quantity ?: 0 }} ${product.key}, ")
        }
        return builder.toString()
    }

    interface OnClickListener {
        fun onItemClicked(position: Int, order: Order?)
    }

    override fun getItemCount(): Int {
        return mItems?.size ?: 0
    }

    fun getItems(): List<Order?>? {
        return this.mItems
    }

    fun updateItems(items: List<Order?>?) {
        this.mItems = items
        notifyDataSetChanged()
    }

}