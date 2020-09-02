package com.appexecutors.cameraxscanner.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity

object PermissionUtils {

    private const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1121

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun addPermission(
        permissionsList: MutableList<String>, permission: String,
        ac: FragmentActivity
    ): Boolean {
        if (ac.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            // Check for Rationale Option
            return ac.shouldShowRequestPermissionRationale(permission)
        }
        return true
    }

    fun checkRequiredPermissions(activity: FragmentActivity, permissionCall: PermissionCallback) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionCall.onPermission(true)
        } else {
            val permissionsNeeded = ArrayList<String>()
            val permissionsList = ArrayList<String>()
            if (!addPermission(permissionsList, Manifest.permission.CAMERA, activity))
                permissionsNeeded.add("CAMERA")
            if (permissionsList.size > 0) {
                activity.requestPermissions(
                    permissionsList.toTypedArray(),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                )
            } else {
                permissionCall.onPermission(true)
            }
        }
    }
}

interface PermissionCallback {
    fun onPermission(approved: Boolean)
}