package com.appexecutors.cameraxscanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.appexecutors.cameraxscanner.camerax.CameraXManager
import com.appexecutors.cameraxscanner.databinding.ActivityScannerBinding
import com.appexecutors.cameraxscanner.utils.PermissionCallback
import com.appexecutors.cameraxscanner.utils.PermissionUtils
import com.appexecutors.cameraxscanner.utils.ScannedData
import com.appexecutors.cameraxscanner.utils.ScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_scanner.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class Scanner : AppCompatActivity() {

    private lateinit var mBinding: ActivityScannerBinding
    private lateinit var cameraXManager: CameraXManager
    private lateinit var scannerOptions: ScannerOptions

    var scanList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scanner)
        scannerOptions = intent?.getSerializableExtra(SCANNER_OPTIONS) as ScannerOptions
        initCameraXManager()
        mBinding.apply {
            lifecycleOwner = this@Scanner

            if (allPermissionsGranted()) {
               Handler().postDelayed({
                   cameraXManager.startCamera()
               }, 200)
            } else {
                ActivityCompat.requestPermissions(
                    this@Scanner,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        mBinding.imageViewClick.setOnClickListener { cameraXManager.takePhoto(ArrayList()) }
    }

    private fun initCameraXManager() {
        cameraXManager = CameraXManager(
            this,
            mBinding.viewFinder,
            this,
            mBinding.graphicOverlayFinder
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    fun imageCaptured(savedUri: Uri){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Display flash animation to indicate that photo was captured
            container.postDelayed({
                container.foreground = ColorDrawable(Color.WHITE)
                container.postDelayed(
                    { container.foreground = null }, 50
                )
            }, 100)
        }

        var image: InputImage? = null
        try {
            image = InputImage.fromFilePath(this, savedUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val scanner = BarcodeScanning.getClient()

        val result = scanner.process(image!!)
            .addOnSuccessListener { barcodes ->
                // Task completed successfully
                for (barcode in barcodes) {
                    Log.e(CameraXManager.TAG, "onImageSaved: ${barcode.displayValue}")
                    if(barcode.displayValue != null) scanList.add(barcode.displayValue!!)
                }

                val intent = Intent()
                val scannedData = ScannedData(scanList, savedUri.toString())
                intent.putExtra(SCANNED_DATA, scannedData)
                setResult(Activity.RESULT_OK, intent)
                finish()

            }
            .addOnFailureListener {
                // Task failed with an exception
                Log.e(CameraXManager.TAG, "onImageSaved: failed" )
                // ...
            }
    }

    fun scanStarted(){
        Log.e("TAG", "scanStarted: $scanList" )
        if (scanList.size > 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                if (scannerOptions.takePhoto) {
                    cameraXManager.takePhoto(scanList)
                }
                else {
                    val intent = Intent()
                    val scannedData = ScannedData(scanList, null)
                    intent.putExtra(SCANNED_DATA, scannedData)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }, 500)
        }
    }



    companion object {

        const val REQUEST_CODE_SCANNER = 10
        private const val REQUEST_CODE_PERMISSIONS = 1
        const val SCANNER_OPTIONS = "SCANNER_OPTIONS"
        const val SCANNED_DATA = "SCANNED_DATA"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        @JvmStatic
        fun startScanner(fragment: Fragment, mScannerOptions: ScannerOptions) {
            PermissionUtils.checkRequiredPermissions(fragment.requireActivity(), object : PermissionCallback{
                override fun onPermission(approved: Boolean) {
                    val mScannerIntent = Intent(fragment.requireActivity(), Scanner::class.java)
                    mScannerIntent.putExtra(SCANNER_OPTIONS, mScannerOptions)
                    mScannerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    fragment.startActivityForResult(mScannerIntent, REQUEST_CODE_SCANNER)
                }
            })
        }

        @JvmStatic
        fun startScanner(activity: FragmentActivity, mScannerOptions: ScannerOptions) {
            PermissionUtils.checkRequiredPermissions(activity, object : PermissionCallback{
                override fun onPermission(approved: Boolean) {
                    val mScannerIntent = Intent(activity, Scanner::class.java)
                    mScannerIntent.putExtra(SCANNER_OPTIONS, mScannerOptions)
                    mScannerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    activity.startActivityForResult(mScannerIntent, REQUEST_CODE_SCANNER)
                }
            })
        }
    }
}