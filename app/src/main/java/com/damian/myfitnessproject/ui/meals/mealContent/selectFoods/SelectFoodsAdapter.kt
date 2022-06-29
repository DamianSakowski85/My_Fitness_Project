package com.damian.myfitnessproject.ui.meals.mealContent.selectFoods

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.damian.myfitnessproject.data.repository.model.FoodItem
import damian.myfitnessproject.databinding.ItemSelectContentBinding

class SelectFoodsAdapter(private val listener: OnItemClickListener) : ListAdapter<FoodItem,
        SelectFoodsAdapter.ViewHolder>(DiffCallback()) {

    var context: Context? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSelectContentBinding.inflate(layoutInflater, parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: ItemSelectContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
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
    }
}