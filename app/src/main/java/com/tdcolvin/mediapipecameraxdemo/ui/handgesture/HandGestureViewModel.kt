package com.tdcolvin.mediapipecameraxdemo.ui.handgesture

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.AndroidViewModel
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizer
import com.google.mediapipe.tasks.vision.gesturerecognizer.GestureRecognizerResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.max

data class HandGestureUiState(
    val mostRecentGesture: String? = null,
)

class HandGestureViewModel(application: Application): AndroidViewModel(application) {
    val uiState = MutableStateFlow(HandGestureUiState())

    private val gestureRecognizer by lazy {
        val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath("gesture_recognizer.task")
        val baseOptions = baseOptionsBuilder.build()

        val optionsBuilder =
            GestureRecognizer.GestureRecognizerOptions.builder()
                .setBaseOptions(baseOptions)
                .setResultListener { result, _ -> handleGestureRecognizerResult(result) }
                .setRunningMode(RunningMode.LIVE_STREAM)

        val options = optionsBuilder.build()
        GestureRecognizer.createFromOptions(getApplication(), options)
    }

    private fun handleGestureRecognizerResult(result: GestureRecognizerResult) {
        // Figure out the most likely gesture
        val bestCategory = result.gestures().firstOrNull()?.maxByOrNull { it.score() }

        // Convert it to an emoji
        val gesture = when(bestCategory?.categoryName()) {
            "Thumb_Up" -> "\uD83D\uDC4D"
            "Thumb_Down" -> "\uD83D\uDC4E"
            "Pointing_Up" -> "☝\uFE0F"
            "Open_Palm" -> "✋"
            "Closed_Fist" -> "✊"
            "Victory" -> "✌\uFE0F"
            "ILoveYou" -> "\uD83E\uDD1F"
            else -> null
        }

        // Display it in the UI
        if (gesture != null) {
            uiState.update { it.copy(mostRecentGesture = gesture) }
        }
    }

    val imageAnalyzer = ImageAnalysis.Analyzer { image ->
        val imageBitmap = image.toBitmap()
        val scale = 500f / max(image.width, image.height)
        // Create a bitmap that's scaled as needed for the model, and rotated as needed to match display orientation
        val scaleAndRotate = Matrix().apply {
            postScale(scale, scale)
            postRotate(image.imageInfo.rotationDegrees.toFloat())
        }
        val scaledAndRotatedBmp = Bitmap.createBitmap(imageBitmap, 0, 0, image.width, image.height, scaleAndRotate, true)

        image.close()

        gestureRecognizer.recognizeAsync(
            BitmapImageBuilder(scaledAndRotatedBmp).build(),
            System.currentTimeMillis()
        )
    }
}