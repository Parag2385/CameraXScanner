package com.appexecutors.cameraxscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.appexecutors.cameraxscanner.utils.ScannerOptions

class Scanner : AppCompatActivity() {

    private lateinit var mBinding: ActivityScannerBinding
    private lateinit var cameraXManager: CameraXManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_scanner)
        initCameraXManager()
        mBinding.apply {
            lifecycleOwner = this@Scanner
        }

        if (allPermissionsGranted()) {
            cameraXManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun initCameraXManager() {
        cameraXManager = CameraXManager(
            this,
            mBinding.viewFinder,
            this
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {

        const val REQUEST_CODE_SCANNER = 10
        private const val REQUEST_CODE_PERMISSIONS = 1
        const val SCANNER_OPTIONS = "SCANNER_OPTIONS"
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