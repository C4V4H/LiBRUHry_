package com.cava.libruhry.compose

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.cava.libruhry.scanner.BarcodeAnalyzer
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    cameraPermissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>,
    onBarcodeDetected: (String) -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        cameraPermissionResultLauncher.launch(
            Manifest.permission.CAMERA
        )
    }
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(localContext)
    }
    val previewView = remember { PreviewView(localContext) }
    var camera: Camera? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder().build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(localContext),
            BarcodeAnalyzer(localContext) { barcode ->
                onBarcodeDetected(barcode)
            }
        )

        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            selector,
            preview,
            imageAnalysis
        )

        onDispose {
            camera?.let {
                cameraProvider.unbindAll()
            }
            preview.setSurfaceProvider(null)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { previewView }
    )
}

// old code may get useful later :P
//
//@Composable
//fun CameraScreen(onBarcodeDetected: (String) -> Unit) {
//    val localContext = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//    val cameraProviderFuture = remember {
//        ProcessCameraProvider.getInstance(localContext)
//    }
//    AndroidView(
//        modifier = Modifier.fillMaxSize(),
//        factory = { context ->
//            val previewView = PreviewView(context)
//            val preview = Preview.Builder().build()
//            val selector = CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build()
//
//            preview.setSurfaceProvider(previewView.surfaceProvider)
//
//            val imageAnalysis = ImageAnalysis.Builder().build()
//            imageAnalysis.setAnalyzer(
//                ContextCompat.getMainExecutor(context),
//                BarcodeAnalyzer(context) { barcode ->
//                    onBarcodeDetected(barcode)
//                }
//            )
//
//            runCatching {
//                cameraProviderFuture.get().bindToLifecycle(
//                    lifecycleOwner,
//                    selector,
//                    preview,
//                    imageAnalysis
//                )
//            }.onFailure {
//                Log.e("CAMERA", "Camera bind error ${it.localizedMessage}", it)
//            }
//            previewView
//        }
//    )
//}