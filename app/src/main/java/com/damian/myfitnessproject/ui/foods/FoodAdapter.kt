package com.damian.myfitnessproject.ui.foods

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damian.myfitnessproject.data.repository.model.FoodItem
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.ItemFoodBinding

class FoodAdapter(private val listener: OnItemClickListener) : ListAdapter<FoodItem,
        FoodAdapter.ViewHolder>(DiffCallback()) {

    var context: Context? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemFoodBinding.inflate(layoutInflater, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.textViewMenu.setOnClickListener {
                val popup = PopupMenu(context, it)
                popup.inflate(R.menu.menu_foods)
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        listener.onMenuItemClick(
                            getItem(bindingAdapterPosition),
                            item
                        )
                    }
                    true
                }
                popup.show()
            }

            binding.apply {
                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val foodItem = getItem(position)
                        listener.onItemClick(foodItem, it)
                    }
                }
            }
        }

        fun bind(foodItem: FoodItem) {
            binding.apply {
                textViewName.text = foodItem.name
                textViewDesc.text = foodItem.description
                mcvItemFood.transitionName = foodItem.id.toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem) =
            oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(foodItem: FoodItem, view: View)
        fun onMenuItemClick(foodItem: FoodItem, menuItem: MenuItem)
    }
}