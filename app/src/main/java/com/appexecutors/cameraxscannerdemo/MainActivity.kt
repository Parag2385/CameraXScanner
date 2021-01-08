package com.appexecutors.cameraxscannerdemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appexecutors.cameraxscanner.Scanner
import com.appexecutors.cameraxscanner.utils.ScannedData
import com.appexecutors.cameraxscanner.utils.ScannerOptions
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val scannerOptions = ScannerOptions()
            scannerOptions.takePhoto = true
            Scanner.startScanner(this, scannerOptions)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Scanner.REQUEST_CODE_SCANNER && resultCode == RESULT_OK && data != null){
            val scannerData = data.getSerializableExtra(Scanner.SCANNED_DATA) as ScannedData

            Log.e("TAG", "onActivityResult: ${scannerData.mScannedList}" )
            val stringBuilder = StringBuilder()
            scannerData.mScannedList.forEach {
                stringBuilder.append(it).append("\n")
            }
            if (stringBuilder.isEmpty()){
                barcode.text = "Couldn't scan the barcode from provided image, please capture again"
            }else barcode.text = stringBuilder.toString()

            Log.e("TAG", "onActivityResult: ${scannerData.mScannedImagePath}" )

            if (scannerData.mScannedImagePath != null) {

                Glide.with(this)
                    .load(scannerData.mScannedImagePath)
                    .into(image)
            }
        }
    }
}