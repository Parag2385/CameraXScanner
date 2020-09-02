package com.appexecutors.cameraxscanner.utils

import java.io.Serializable

class ScannerOptions : Serializable{
    companion object{
        @JvmStatic
        fun init(): ScannerOptions{
            return ScannerOptions()
        }
    }
}