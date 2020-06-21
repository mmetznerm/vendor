package br.com.mmetzner.vendor.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.com.mmetzner.vendor.R
import br.com.mmetzner.vendor.model.Client

class ClientAdapter(context: Context?) : BaseAdapter() {

    private var items: Array<Client> = arrayOf()

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.spinner_item, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        vh.label.text = items[position].name
        return view
    }

    override fun getItem(position: Int): Client {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return items.size
    }

    fun getItems(): Array<Client> {
        return items
    }

    fun addAll(items: Array<Client>) {
        this.items = items
        notifyDataSetChanged()
    }

    private class ItemRowHolder(row: View?) {
        val label: TextView = row?.findViewById(R.id.tvDescription) as TextView
    }
}