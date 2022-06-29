package com.damian.myfitnessproject.ui.foods.addEdit

import android.os.Bundle
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import damian.myfitnessproject.R
import damian.myfitnessproject.databinding.FragmentFoodTakePhotoBinding
import kotlinx.coroutines.flow.collect
import java.util.concurrent.ExecutionException

@AndroidEntryPoint
class TakePhotoFragment : Fragment(R.layout.fragment_food_take_photo) {

    companion object {
        const val REQUEST_KEY = "take_photo_fragment"
        const val KEY = "photo_result"
    }

    private var imageCapture: ImageCapture? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private val viewModel: TakePhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
            /* forward= */ true
        )
        returnTransition = MaterialSharedAxis(
            MaterialSharedAxis.Z,
            /* forward= */ false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentFoodTakePhotoBinding.bind(view)
        initCamera(binding.previewView)
        binding.fabTakeImage.setOnClickListener {
            viewModel.onTakeImageClick()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow
                .collect {
                    when (it) {
                        is TakePhotoViewModel.Event.NavToAddEditFood ->
                            navToAddEditFood(it.byteArray)
                        is TakePhotoViewModel.Event.TakeImageEvent -> takeImage()
                    }
                }
        }
    }

    private fun initCamera(previewView: PreviewView) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture!!.addListener({
            try {
                // Camera provider is now guaranteed to be available
                val cameraProvider = cameraProviderFuture!!.get()
                // Set up the view finder use case to display camera preview
                val preview = Preview.Builder()
                    .build()
                preview.setSurfaceProvider(previewView.surfaceProvider)
                // Choose the camera by requiring a lens facing
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                // Set up the capture use case to allow users to take photos
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                // Attach use cases to the camera with the same lifecycle owner
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,  // imageAnalysis,
                    preview,
                    imageCapture
                )
            } catch (e: InterruptedException) {
                // Currently no exceptions thrown. cameraProviderFuture.get()
                // shouldn't block since the listener is being called, so no need to
                // handle InterruptedException.
            } catch (e: ExecutionException) {
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takeImage() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        imageCapture!!.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                viewModel.onImageTaken(image)
                image.close()
            }
        })
    }

    private fun navToAddEditFood(byteArray: ByteArray) {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(KEY to byteArray)
        )
        findNavController().popBackStack()
    }
}