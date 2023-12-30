package com.example.adminorderapp.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.adminorderapp.R
import com.example.adminorderapp.data.store.StoreUiView
import com.example.adminorderapp.databinding.StoreItemLayoutBinding

class StoreAdapter(
    private val callback : (Long,String) -> Unit
) : ListAdapter<StoreUiView,StoreAdapter.StoreViewHolder>(diffCallback){
    class StoreViewHolder(private val binding: StoreItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): StoreViewHolder {
                val binding = StoreItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return StoreViewHolder(binding)
            }
        }
        fun bind(store : StoreUiView,callback: (Long, String) -> Unit){
            binding.storeName.text = binding.root.context.getString(R.string.name,store.address)
            binding.root
            binding.root.setOnClickListener {
                callback.invoke(store.id,store.address.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StoreViewHolder.create(parent)

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        holder.bind(getItem(position),callback)
    }
}
private val diffCallback = object : DiffUtil.ItemCallback<StoreUiView>(){
    override fun areItemsTheSame(oldItem: StoreUiView, newItem: StoreUiView): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StoreUiView, newItem: StoreUiView): Boolean {
        return oldItem == newItem
    }

}