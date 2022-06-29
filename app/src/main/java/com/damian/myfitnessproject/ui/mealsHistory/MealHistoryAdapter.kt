package com.damian.myfitnessproject.ui.mealsHistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damian.myfitnessproject.data.database.entity.Meal
import damian.myfitnessproject.databinding.ItemMealBinding
import damian.myfitnessproject.databinding.ItemMealHistoryBinding

class MealHistoryAdapter(private val listener: OnItemClickListener) : ListAdapter<Meal,
        MealHistoryAdapter.ViewHolder>(DiffCallback()) {

    var context: Context? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMealHistoryBinding.inflate(layoutInflater, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemMealHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

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
                textViewMealName.text = meal.name
                textViewMealTime.text = meal.time
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
    }
}