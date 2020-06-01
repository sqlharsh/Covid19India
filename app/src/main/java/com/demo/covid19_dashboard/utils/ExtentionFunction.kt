package com.demo.covid19_dashboard.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.demo.covid19_dashboard.utils.Constants.Companion.PREF_ADDRESS_LATLNG
import com.google.android.gms.maps.model.LatLng
import com.iMpactHealth.iwire.solutions.utils.SharedPreferenceHelper

fun <T> Activity.gotoActivity(
    activity: Class<T>,
    bundle: Bundle? = null,
    needToFinish: Boolean = true,
    clearAllActivity: Boolean = false
) {
    val intent = Intent(this, activity)

    if (bundle != null)
        intent.putExtras(bundle)

    if (clearAllActivity)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

    startActivity(intent)

    if (needToFinish)
        finish()

}


fun LatLng?.saveLatlng(context:Context) {
    val latlngStr = this?.latitude.toString().plus(",").plus(this?.longitude.toString())
    SharedPreferenceHelper.getInstance(context).setValue(PREF_ADDRESS_LATLNG,latlngStr)
}

fun String?.getSavedLatlng():LatLng? {
    val latlngArray = this?.split(",")?.toTypedArray()
    if (!latlngArray?.get(0).isNullOrBlank() && latlngArray?.size!! > 1 && !latlngArray.get(1).isBlank()   )
        return latlngArray.get(0).toDouble().let { LatLng(it,latlngArray.get(1).toDouble()) }
    else return null
}
