package com.appexecutors.cameraxscanner.utils

import java.io.Serializable

data class ScannedData(
    var mScannedList: ArrayList<String>,
    var mScannedImagePath: String?
): Serializable