package com.mikore.ppqr.utility

import android.content.res.Resources
import android.util.DisplayMetrics

object Utils {
    fun screenWidthDp(resources: Resources): Int {
        val density = resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT
        return (resources.displayMetrics.widthPixels / density).toInt()
    }

    fun screenWidth(resources: Resources): Int = resources.displayMetrics.widthPixels

    fun screenHeight(resources: Resources): Int = resources.displayMetrics.heightPixels

    fun screenDensity(resources: Resources): Float = resources.displayMetrics.density
}
