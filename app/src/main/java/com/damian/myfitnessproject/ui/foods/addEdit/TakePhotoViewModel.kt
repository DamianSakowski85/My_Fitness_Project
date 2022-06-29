package com.damian.myfitnessproject.ui.foods.addEdit

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damian.myfitnessproject.data.repository.ImageRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TakePhotoViewModel @Inject constructor(
    private val imageRepo: ImageRepo
) : ViewModel() {

    fun onImageTaken(imageProxy: ImageProxy) = viewModelScope.launch {
        val byteArray = imageRepo.convertImageProxyToByteArray(imageProxy)
        eventChannel.send(Event.NavToAddEditFood(byteArray))
    }

    fun onTakeImageClick() = viewModelScope.launch {
        eventChannel.send(Event.TakeImageEvent)
    }

    sealed class Event {
        data class NavToAddEditFood(val byteArray: ByteArray) : Event() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as NavToAddEditFood

                if (!byteArray.contentEquals(other.byteArray)) return false

                return true
            }

            override fun hashCode(): Int {
                return byteArray.contentHashCode()
            }
        }

        object TakeImageEvent : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()
}