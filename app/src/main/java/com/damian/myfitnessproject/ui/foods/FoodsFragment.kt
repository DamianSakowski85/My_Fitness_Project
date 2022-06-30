package com.damian.myfitnessproject.ui.foods

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.repository.model.FoodItem
import com.damian.myfitnessproject.ui.utli.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentFoodsBinding

@AndroidEntryPoint
class FoodsFragment : Fragment(R.layout.fragment_foods), FoodAdapter.OnItemClickListener {

    private val viewModel: FoodsViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        exitTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ true
        )
        reenterTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        val binding = FragmentFoodsBinding.bind(view)

        val adapter = FoodAdapter(this)

        binding.apply {
            rvFoods.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            fabAddItem.setOnClickListener {
                viewModel.onAddItemClick()
            }
        }

        viewModel.allFoodItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.tvEmptyList.visibility = View.VISIBLE
            } else {
                binding.tvEmptyList.visibility = View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is FoodsViewModel.Event.NavToAddItem ->
                            navToAddItem()
                        is FoodsViewModel.Event.NavToEditItem ->
                            navToEditItem(it.wholeFood)
                        is FoodsViewModel.Event.NavToDeleteItem ->
                            navToDeleteItem(it.foodItem)
                        is FoodsViewModel.Event.NavToDetails ->
                            navToDetails(it.wholeFood, it.extras, it.macroTitle)
                        is FoodsViewModel.Event.ShowMessage ->
                            Snackbar.make(view, getString(it.resId), Snackbar.LENGTH_LONG).show()
                    }
                }
        }

        setFragmentResultListener(getString(R.string.add_edit_food_request_key)) { _, bundle ->
            val result = bundle.getInt(getString(R.string.result))
            viewModel.onAddEditResult(result)
        }

        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onItemClick(foodItem: FoodItem, view: View) {
        val extras = FragmentNavigatorExtras(view to view.transitionName)
        viewModel.onItemClick(foodItem, extras)
    }

    override fun onMenuItemClick(foodItem: FoodItem, menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.action_update -> {
                viewModel.onEditItemClick(foodItem)
            }
            R.id.action_delete -> {
                viewModel.onDeleteItemClick(foodItem)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(
                pendingQuery,
                false
            )
        }

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    private fun navToDeleteItem(foodItem: FoodItem) {
        val action = FoodsFragmentDirections.actionNavFoodsToDeleteFoodDialog(foodItem)
        findNavController().navigate(action)
    }

    private fun navToAddItem() {
        val action = FoodsFragmentDirections.actionNavFoodsToAddEditFoodFragment("Add food")
        findNavController().navigate(action)
    }

    private fun navToEditItem(food: Food) {
        val action = FoodsFragmentDirections
            .actionNavFoodsToAddEditFoodFragment("Edit food")
        action.food = food
        findNavController().navigate(action)
    }

    private fun navToDetails(
        wholeFood: Food,
        extras: FragmentNavigator.Extras,
        macroTitle: String
    ) {
        val action = FoodsFragmentDirections.actionGlobalNavFoodDetails(wholeFood, macroTitle,100)
        findNavController().navigate(action, extras)
    }

    override fun onPause() {
        super.onPause()
        searchView.setOnQueryTextListener(null)
    }
}