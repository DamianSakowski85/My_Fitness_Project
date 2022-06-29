package com.damian.myfitnessproject.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import java.io.*
import javax.inject.Inject

class ImageRepo @Inject constructor(private val filesDir: File) {
    fun convertImageProxyToByteArray(imageProxy: ImageProxy): ByteArray {
        val byteBuffer = imageProxy.planes[0].buffer
        byteBuffer.rewind()
        val bytes = ByteArray(byteBuffer.capacity())
        byteBuffer[bytes]
        return bytes.clone()
    }

    fun convertByteArrayToBitmap(byteArray: ByteArray): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

        val width = bitmap.width
        val height = bitmap.height

        val outputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

        val matrix = Matrix()
        matrix.postRotate(90.toFloat())

        val fullSizeBitmap = Bitmap.createBitmap(
            outputBitmap, 0, 0,
            outputBitmap.width, outputBitmap.height,
            matrix, true
        )

        return crop(fullSizeBitmap)
    }

    fun saveToInternalStorage(bitmapImage: Bitmap, bitmapName: String): String {
        //test directory
        val file = File(filesDir, "$bitmapName.jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                assert(fos != null)
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file.absolutePath
    }

    fun loadBitmap(path: String): Bitmap? {
        val f = File(path)
        var b: Bitmap? = null
        try {
            b = BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return b
    }

    fun deleteBitmap(path: String) {
        val file = File(path)
        file.delete()
    }

    private fun crop(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val boxHeight = h / 8
        val start = boxHeight * 2
        val stop = boxHeight * 4
        return Bitmap.createBitmap(bitmap, 0, start, w, stop)
    }
}