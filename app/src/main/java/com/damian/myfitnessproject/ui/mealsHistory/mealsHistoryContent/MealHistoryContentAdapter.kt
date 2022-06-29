package com.damian.myfitnessproject.ui.mealsHistory.mealsHistoryContent

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damian.myfitnessproject.data.repository.model.MealContentItem
import damian.myfitnessproject.databinding.ItemMealHistoryContentBinding

class MealHistoryContentAdapter(private val listener: OnItemClickListener) :
    ListAdapter<MealContentItem, MealHistoryContentAdapter.ViewHolder>(DiffCallback()) {

    var context: Context? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMealHistoryContentBinding.inflate(layoutInflater, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemMealHistoryContentBinding) :
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

        fun bind(mealContentItem: MealContentItem) {
            binding.apply {
                textViewFoodName.text = mealContentItem.name
                textViewFoodDesc.text = mealContentItem.description
                textViewPortionSize.text = mealContentItem.portion.size.toString()
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
    }
}