package br.com.mmetzner.vendor.helper.route

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Product
import kotlinx.android.synthetic.main.activity_select_product_item.view.*

class StartRouteAdapter(private var mItems: List<Product?>) : RecyclerView.Adapter<StartRouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartRouteViewHolder {
        return StartRouteViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_select_product_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StartRouteViewHolder, position: Int) {
        val item = mItems[position]

        holder.itemView.tvDescription.text = item?.description
        holder.itemView.etQuantity.setText(item?.quantity.toString())
        holder.itemView.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().isNotEmpty()) {
                    mItems[position]?.quantity = s.toString().toInt()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateItems(items: List<Product?>) {
        this.mItems = items
        notifyDataSetChanged()
    }

    fun getItems(): List<Product?> {
        return mItems
    }

}