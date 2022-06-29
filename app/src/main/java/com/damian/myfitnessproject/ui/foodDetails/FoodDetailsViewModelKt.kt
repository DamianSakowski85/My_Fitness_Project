package com.damian.myfitnessproject.ui.foodDetails

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.damian.myfitnessproject.data.database.entity.Food
import com.damian.myfitnessproject.data.repository.ImageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodDetailsViewModelKt @Inject constructor(
    private val imageRepo: ImageRepo,
    state: SavedStateHandle
) : ViewModel() {
    private val _wholeFood = MutableLiveData<Food>()
    private val _image = MutableLiveData<Bitmap>()

    val wholeFood: LiveData<Food> = _wholeFood
    val image: LiveData<Bitmap> = _image

    var name: String = ""
    var desc: String = ""
    var transName = ""
    var title = ""
    var portionSize = 0

    init {
        val food = state.get<Food>("food")
        val macroTitle = state.get<String>("macro_title")
        portionSize = state.get<Int>("portionSize")?:0

        macroTitle?.let {
            title = it
        }

        food?.let {
            name = it.name
            desc = it.description

            transName = it.id.toString()
            _wholeFood.value = it
            if (it.imagePath.isNotBlank()) {
                viewModelScope.launch {
                    val bitmap = imageRepo.loadBitmap(it.imagePath)
                    _image.apply {
                        value = bitmap
                    }
                }
            }
        }
    }
}