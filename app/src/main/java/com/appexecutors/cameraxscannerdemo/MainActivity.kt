package com.appexecutors.cameraxscannerdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appexecutors.cameraxscanner.Scanner
import com.appexecutors.cameraxscanner.utils.ScannerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            Scanner.startScanner(this, ScannerOptions())
        }
    }
}