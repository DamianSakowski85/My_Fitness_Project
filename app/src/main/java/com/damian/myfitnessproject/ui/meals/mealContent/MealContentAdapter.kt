package com.damian.myfitnessproject.ui.meals.mealContent

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.ItemMealContentBinding


class MealContentAdapter(private val listener: OnItemClickListener) :
    ListAdapter<MealContentItem, MealContentAdapter.ViewHolder>(DiffCallback()) {

    var context: Context? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMealContentBinding.inflate(layoutInflater, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemMealContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvOptions.setOnClickListener {
                val popup = PopupMenu(context, it)
                popup.inflate(R.menu.menu_meal_content)
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
                        val meal = getItem(position)
                        listener.onItemClick(meal, it)
                    }
                }
            }
        }

        fun bind(mealContentItem: MealContentItem) {
            binding.apply {
                textViewName.text = mealContentItem.name
                textViewDesc.text = mealContentItem.description
                textViewPortion.text = mealContentItem.portion.size.toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MealContentItem>() {
        override fun areItemsTheSame(oldItem: MealContentItem, newItem: MealContentItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: MealContentItem, newItem: MealContentItem) =
            oldItem == newItem
    }


    interface OnItemClickListener {
        fun onItemClick(mealContentItem: MealContentItem, view: View)
        fun onMenuItemClick(mealContentItem: MealContentItem, menuItem: MenuItem)
    }
}