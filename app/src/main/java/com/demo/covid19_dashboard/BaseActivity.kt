package com.demo.covid19_dashboard

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.demo.covid19_dashboard.utils.Constants.Companion.REQUEST_CHECK_LOCATION_SETTINGS
import com.demo.covid19_dashboard.viewmodels.MainRepository
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.nextec.listener.SnackbarListener

open class BaseActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private var progressDialog:Dialog? = null

    companion object {
        @Volatile
        private var manager: LocationManager? = null

        fun getInstance(context:Context):LocationManager {
            manager ?: synchronized(this) {
                manager =  context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            }
            return manager as LocationManager
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (!getInstance(this).isProviderEnabled(LocationManager.GPS_PROVIDER))
            enableGps()
    }

    protected fun isLocationPermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        )
            return true
        return false
    }
    protected fun showSnackbar(
        view: View,
        message: String,
        time: Int,
        snackbarListener: SnackbarListener
    ) {
        val snackbar = Snackbar.make(view, message, time)
            .setAction(getString(R.string.go_to_settings), View.OnClickListener {
                snackbarListener.onSnackbarClick()
            })
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackbar.show()
    }

    // enable gps
    fun enableGps() {

        //Create LocationSettingRequest object using locationRequest
        val locationSettingBuilder = LocationSettingsRequest.Builder()
        locationSettingBuilder.addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        val locationSetting = locationSettingBuilder.build()

        val task: Task<LocationSettingsResponse>

        //Need to check whether location settings are satisfied
        val settingsClient = LocationServices.getSettingsClient(this)
        task = settingsClient?.checkLocationSettings(locationSetting)!!

        task.addOnSuccessListener { locationSettingsResponse ->
            Log.d(TAG, "onSuccess: " + locationSettingsResponse.locationSettingsStates)

        }
        task.addOnFailureListener(this, OnFailureListener { e ->
            val statusCode = (e as ApiException).statusCode
            when (statusCode) {
                CommonStatusCodes.RESOLUTION_REQUIRED ->
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        val resolvable = e as ResolvableApiException
                        resolvable.startResolutionForResult(
                            this,
                            REQUEST_CHECK_LOCATION_SETTINGS
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                }
            }// Location settings are not satisfied. However, we have no way
            // to fix the settings so we won't show the dialog.
        })
        task.addOnSuccessListener {

        }
    }

    fun showProgressDialog() {
            progressDialog = Dialog(this);

            progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            try {
                val dividerId = progressDialog?.context?.resources?.getIdentifier(
                    "android:id/titleDivider",
                    null,
                    null
                )
                val divider = dividerId?.let { progressDialog?.findViewById<View>(it) }
                divider?.setBackgroundColor(this.resources.getColor(android.R.color.transparent))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val mTitle = progressDialog?.findViewById<View>(android.R.id.title) as TextView?
                if (mTitle != null)
                    mTitle.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                val x = Resources.getSystem().getIdentifier("titleDivider", "id", "android")
                val titleDivider = progressDialog?.findViewById<View>(x)
                titleDivider?.setBackgroundColor(this.resources.getColor(android.R.color.transparent))

            } catch (e: Exception) {
                e.printStackTrace()
            }

            progressDialog?.setContentView(R.layout.custom_progressbar)
            progressDialog?.setCancelable(false)

            progressDialog?.show()
    }

    fun hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog?.isShowing!!) {
                progressDialog?.dismiss()
            }
        } catch (throwable: Throwable) {

        } finally {
            progressDialog?.dismiss()
        }
    }

    fun isNetworkAvailable(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return if (netInfo != null && netInfo.isConnectedOrConnecting
            && cm.activeNetworkInfo.isAvailable
            && cm.activeNetworkInfo.isConnected
        ) {
            true
        } else {
            showToast(getString(R.string.no_internet_message))
            false
        }
    }


    fun showToast(msg: String) {
        val toast =
            Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 120)
        toast.show()
    }
}
