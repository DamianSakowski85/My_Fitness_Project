package com.damian.myfitnessproject.ui.meals

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damian.myfitnessproject.data.database.entity.Meal
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.ItemMealBinding

class MealAdapter(private val listener: OnItemClickListener) : ListAdapter<Meal,
        MealAdapter.ViewHolder>(DiffCallback()) {

    var context: Context? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMealBinding.inflate(layoutInflater, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.textViewMenu.setOnClickListener {
                val popup = PopupMenu(context, it)
                popup.inflate(R.menu.menu_meals)
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

        fun bind(meal: Meal) {
            binding.apply {
                textViewName.text = meal.name
                textViewTime.text = meal.time
                mcvItemMeal.transitionName = meal.id.toString()
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal) =
            oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(meal: Meal, view: View)
        fun onMenuItemClick(meal: Meal, menuItem: MenuItem)
    }
}