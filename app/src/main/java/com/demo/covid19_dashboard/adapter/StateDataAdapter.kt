package com.demo.covid19_dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.demo.covid19_dashboard.R
import com.demo.covid19_dashboard.databinding.ListItemDataBinding
import com.demo.covid19_dashboard.databinding.ListItemHeaderBinding
import com.demo.covid19_dashboard.models.Statewise
import com.demo.covid19_dashboard.utils.Constants.Companion.HEADER
import com.demo.covid19_dashboard.utils.Constants.Companion.ITEM

class StateDataAdapter(private var mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var layoutInflater: LayoutInflater
    private var listModel: MutableList<Statewise>? = null

    init {

        this.listModel = ArrayList()
        layoutInflater = LayoutInflater.from(mContext)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            ITEM -> {
                val binding =
                    DataBindingUtil.inflate<ListItemDataBinding>(
                        layoutInflater, R.layout.list_item_data, parent, false)
                viewHolder = ItemViewHolder(binding)
            }
            HEADER -> {
                val binding =
                    DataBindingUtil.inflate<ListItemHeaderBinding>(layoutInflater,R.layout.list_item_data, parent, false)

                viewHolder = HeaderViewHolder(binding)
            }
            else-> {
                val binding =
                    DataBindingUtil.inflate<ListItemDataBinding>(
                        layoutInflater, R.layout.list_item_data, parent, false)
                viewHolder = ItemViewHolder(binding)
            }
        }
        return viewHolder
    }

   override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val viewholder = holder as ItemViewHolder
                val member = listModel?.get(position)
                member?.let { viewholder.bind(it) }
            }
            HEADER -> {
                val loadingViewHolder = holder as HeaderViewHolder
                val member = listModel?.get(position)
                member?.let { loadingViewHolder.bind(it) }
            }
        }
    }

    override fun getItemCount(): Int {
        return (if (listModel == null) 0 else listModel?.size)!!
    }

    /* this method will decide which type of layout to show on position */
    override fun getItemViewType(position: Int): Int {
        return if (listModel?.get(position)?.type == HEADER) HEADER else ITEM
    }

    /* add all data */
    fun addAllData(listModel: List<Statewise>?) {
        listModel?.let { this.listModel?.addAll(it) }
        notifyDataSetChanged()
    }

    fun getData(): List<Statewise?>? {
        return listModel
    }

    class ItemViewHolder internal constructor(private val binding: ListItemDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { }
        }
        fun bind(model:Statewise){
            binding.model = model
            binding.layoutCases.model = model
            binding.executePendingBindings()
        }
    }

    /* view holder for bottom pagination progress bar*/
    class HeaderViewHolder internal constructor(private val binding: ListItemHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

        }
        fun bind(model:Statewise){
            binding.model = model
            binding.executePendingBindings()
        }
    }

}