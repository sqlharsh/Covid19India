package com.demo.covid19_dashboard.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle

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
