package com.damian.myfitnessproject.ui.meals.mealContent.selectFoods

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.damian.myfitnessproject.data.repository.model.FoodItem
import com.damian.myfitnessproject.ui.utli.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentSelectFoodsBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SelectFoodsFragment : Fragment(R.layout.fragment_select_foods),
    SelectFoodsAdapter.OnItemClickListener {

    private val viewModel: SelectFoodViewModel by viewModels()
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
        enterTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ true
        )
        returnTransition = MaterialSharedAxis(
            MaterialSharedAxis.X,
            /* forward= */ false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()

        val binding = FragmentSelectFoodsBinding.bind(view)
        val adapter = SelectFoodsAdapter(this)

        binding.apply {
            rvFoods.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.nonSelectedFoods.observe(viewLifecycleOwner) { foods ->
            adapter.submitList(foods)
            if (foods.isEmpty()) {
                binding.textViewEmptyList.visibility = View.VISIBLE
            } else {
                binding.textViewEmptyList.visibility = View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is SelectFoodViewModel.Event.NavToAddPortion -> {
                            val action = SelectFoodsFragmentDirections
                                .actionSelectFoodsFragmentToAddPortionFragment(
                                    it.title,
                                    it.meal,
                                    it.foodItem
                                )
                            findNavController().navigate(action)
                        }
                        is SelectFoodViewModel.Event.ShowMessage -> {
                            Snackbar.make(view, getString(it.resId), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        setFragmentResultListener(getString(R.string.add_portion_request_key)) { _, bundle ->
            val result = bundle.getInt(getString(R.string.result))
            viewModel.onAddPortionResult(result)
        }


        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onItemClick(foodItem: FoodItem, view: View) {
        viewModel.onItemClick(foodItem)
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

    override fun onPause() {
        super.onPause()
        searchView.setOnQueryTextListener(null)
    }
}