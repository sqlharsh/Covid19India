package com.demo.covid19_dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.demo.covid19_dashboard.databinding.ActivityLocationPermissionBinding
import com.demo.covid19_dashboard.utils.Constants
import com.demo.covid19_dashboard.utils.Constants.Companion.LOCATION_REQUESTCODE
import com.demo.covid19_dashboard.utils.Constants.Companion.REQUEST_CODE_SETTINGS_LOCATION
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.nextec.listener.SnackbarListener

class LocationPermissionActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding:ActivityLocationPermissionBinding
    val locationPermissionList = listOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_location_permission)
        initView()
    }
    private fun initView(){

        binding.clicklistener = this
        val manager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            enableGps()

        if (isLocationPermissionGranted())
            startMainActivity()
    }


    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted())
            requestPermissions(locationPermissionList.toTypedArray(), LOCATION_REQUESTCODE)
    }

    override fun onClick(p0: View?) {
        requestPermission()
    }

    // handle permission result here
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_REQUESTCODE -> {
                handlePermissionResult()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_SETTINGS_LOCATION -> {
                if (isLocationPermissionGranted())
                    startMainActivity()
            }
        }
    }
    private fun handlePermissionResult() {
        if (!isLocationPermissionGranted())
            showSnackbar(binding.container,
            getString(R.string.allow_permission_to_use_some_feature), Snackbar.LENGTH_LONG,
            object : SnackbarListener {
                override fun onSnackbarClick() {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        , Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    )
                    startActivityForResult(intent, REQUEST_CODE_SETTINGS_LOCATION)
                }
            })
       else  startMainActivity()
    }
    private fun startMainActivity(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}
