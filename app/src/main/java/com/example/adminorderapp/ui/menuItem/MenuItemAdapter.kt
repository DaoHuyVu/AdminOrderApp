package com.example.adminorderapp.ui.menuItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.adminorderapp.R
import com.example.adminorderapp.util.URL_PREFIX
import com.example.adminorderapp.data.menuItem.MenuItemUiView
import com.example.adminorderapp.databinding.MenuItemLayoutBinding

class MenuItemAdapter(
    private val callback : (Long,String) -> Unit
) : ListAdapter<MenuItemUiView, MenuItemAdapter.MenuItemViewHolder>(diffCallback) {
    class MenuItemViewHolder(private val binding: MenuItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): MenuItemViewHolder {
                val binding = MenuItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MenuItemViewHolder(binding)
            }
        }
        fun bind(menuItem : MenuItemUiView,callback: (Long, String) -> Unit){
            binding.menuItemName.text = binding.root.context.getString(R.string.name,menuItem.name)
            binding.menuItemPrice.text = binding.root.context.getString(R.string.item_price,menuItem.price)
            Glide
                .with(binding.root)
                .load(URL_PREFIX + menuItem.imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(android.R.drawable.stat_notify_error)
                .into(binding.menuItemImage)
            binding.root.setOnClickListener {
                callback.invoke(menuItem.id,menuItem.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuItemViewHolder.create(parent)

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(getItem(position),callback)
    }
}
private val diffCallback = object : DiffUtil.ItemCallback<MenuItemUiView>(){
    override fun areItemsTheSame(oldItem: MenuItemUiView, newItem: MenuItemUiView): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MenuItemUiView, newItem: MenuItemUiView): Boolean {
        return oldItem == newItem
    }


}