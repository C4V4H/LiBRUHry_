package com.cava.libruhry.scanner

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage


class BarcodeAnalyzer(private val context: Context, private val onBarcodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(
                image,
                imageProxy.imageInfo.rotationDegrees
            )

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.let { barcode ->
                        val barcodeValue = barcode.rawValue ?: ""
                        //@TODO fare si che controlli che le ultime 10 registrazioni siano corrette prima di confermare
                        println(barcodeValue)
                        onBarcodeDetected(barcodeValue)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("BarcodeAnalyzer", "Error processing barcode: $exception")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}