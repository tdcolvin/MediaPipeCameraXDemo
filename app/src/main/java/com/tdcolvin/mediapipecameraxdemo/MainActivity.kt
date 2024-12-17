package com.tdcolvin.mediapipecameraxdemo

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tdcolvin.mediapipecameraxdemo.ui.handgesture.HandGestureScreen
import com.tdcolvin.mediapipecameraxdemo.ui.theme.MediaPipeCameraXDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaPipeCameraXDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WithPermission(
                        modifier = Modifier.padding(innerPadding),
                        permission = Manifest.permission.CAMERA
                    ) {
                        HandGestureScreen()
                    }
                }
            }
        }
    }
}