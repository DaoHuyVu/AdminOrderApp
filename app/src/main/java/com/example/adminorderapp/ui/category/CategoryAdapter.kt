package com.example.adminorderapp.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.adminorderapp.R
import com.example.adminorderapp.api.category.Category
import com.example.adminorderapp.databinding.CategoryItemLayoutBinding
import com.example.adminorderapp.util.URL_PREFIX

class CategoryAdapter(
    private val callback : (Long,String) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(diffCallback){
    class CategoryViewHolder(private val binding: CategoryItemLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun create(parent: ViewGroup): CategoryViewHolder {
                val binding = CategoryItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CategoryViewHolder(binding)
            }
        }
        fun bind(category: Category,callback: (Long,String) -> Unit){
           binding.apply {
               categoryName.text = binding.root.context.getString(R.string.name,category.name)
               Glide
                   .with(root)
                   .load(URL_PREFIX + category.imageUrl)
                   .error(android.R.drawable.stat_notify_error)
                   .transition(DrawableTransitionOptions.withCrossFade())
                   .into(categoryImage)
               root.setOnClickListener{ callback.invoke(category.id,category.name) }
           }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder.create(parent)

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position),callback)
    }
}
private val diffCallback = object : DiffUtil.ItemCallback<Category>(){
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }

}