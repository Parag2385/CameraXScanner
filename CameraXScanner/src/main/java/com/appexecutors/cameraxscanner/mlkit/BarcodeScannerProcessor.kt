package com.appexecutors.cameraxscanner.mlkit

import android.content.Context
import android.graphics.Rect
import android.util.Log
import com.appexecutors.cameraxscanner.Scanner
import com.appexecutors.cameraxscanner.camerax.BaseImageAnalyzer
import com.appexecutors.cameraxscanner.camerax.GraphicOverlay
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.IOException

class BarcodeScannerProcessor(private val view: GraphicOverlay, private val context: Context) :
    BaseImageAnalyzer<List<Barcode>>() {

    private val options = BarcodeScannerOptions.Builder().build()
    private val scanner = BarcodeScanning.getClient(options)

    override val graphicOverlay: GraphicOverlay
        get() = view

    override fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return scanner.process(image)
    }

    override fun stop() {
        try {
            Log.e(TAG, "stop: " )
            scanner.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Barcode Scanner: $e")
        }
    }

    private var scanned = false

    override fun onSuccess(
        results: List<Barcode>,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        graphicOverlay.clear()
        results.forEach {
            val barcodeGraphic = BarcodeGraphic(graphicOverlay, it, rect)
            if(!it.displayValue.isNullOrEmpty()){
                if (!((context as Scanner).scanList.contains(it.displayValue)))
                context.scanList.add(it.displayValue!!)

                Log.e(TAG, "onSuccess: ${context.scanList}" )

                if (context.scanList.size == 1 && !scanned) {
                    scanned = true
                    context.scanStarted()
                }
            }

            graphicOverlay.add(barcodeGraphic)
        }
        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Barcode Scan failed.$e")
    }

    companion object {
        private const val TAG = "BarcodeScanProcessor"
    }

}