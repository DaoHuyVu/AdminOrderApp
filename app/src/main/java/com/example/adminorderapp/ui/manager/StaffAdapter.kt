package com.example.adminorderapp.ui.manager

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.adminorderapp.R
import com.example.adminorderapp.api.manager.StaffUiView
import com.example.adminorderapp.databinding.StaffItemLayoutBinding

class StaffAdapter(
    private val callback : (Long,String) -> Unit
) :ListAdapter<StaffUiView,StaffAdapter.StaffViewHolder>(diffCallback) {
    class StaffViewHolder(private val binding: StaffItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): StaffViewHolder {
                val binding = StaffItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return StaffViewHolder(binding)
            }
        }

        fun bind(staff : StaffUiView, callback: (Long, String) -> Unit){
            binding.staffId.text = binding.root.context.getString(R.string.id,staff.id)
            binding.store.text = binding.root.context.getString(R.string.order_location,staff.address.toString())
            binding.staffName.text = binding.root.context.getString(R.string.name,staff.name)
            binding.staffEmail.text = binding.root.context.getString(R.string.email,staff.email)
            binding.root.setOnClickListener {
                callback.invoke(staff.id,staff.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StaffViewHolder.create(parent)

    override fun onBindViewHolder(holder: StaffViewHolder, position: Int) {
        holder.bind(getItem(position),callback)
    }
}
private val diffCallback = object : DiffUtil.ItemCallback<StaffUiView>(){
    override fun areItemsTheSame(oldItem: StaffUiView, newItem: StaffUiView): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StaffUiView, newItem: StaffUiView): Boolean {
        return oldItem == newItem
    }

}