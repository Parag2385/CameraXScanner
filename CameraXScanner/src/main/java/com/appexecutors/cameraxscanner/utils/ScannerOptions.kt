package com.appexecutors.cameraxscanner.utils

import java.io.Serializable

class ScannerOptions : Serializable{

    var takePhoto = false

    companion object{
        @JvmStatic
        fun init(): ScannerOptions{
            return ScannerOptions()
        }
    }
}