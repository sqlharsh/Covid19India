package com.demo.covid19_dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.demo.covid19_dashboard.databinding.ActivityLocationPermissionBinding
import com.demo.covid19_dashboard.dialog.AddressInfoDialogFragment
import com.demo.covid19_dashboard.listeners.OnItemClickListener
import com.demo.covid19_dashboard.utils.Constants
import com.demo.covid19_dashboard.utils.Constants.Companion.LOCATION_REQUESTCODE
import com.demo.covid19_dashboard.utils.Constants.Companion.REQUEST_CODE_SETTINGS_LOCATION
import com.demo.covid19_dashboard.utils.getSavedLatlng
import com.demo.covid19_dashboard.utils.gotoActivity
import com.demo.covid19_dashboard.utils.saveLatlng
import com.demo.covid19_dashboard.viewmodels.PermissionViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.iMpactHealth.iwire.solutions.utils.SharedPreferenceHelper
import com.nextec.listener.SnackbarListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class LocationPermissionActivity : BaseActivity(), View.OnClickListener {

    private val TAG = LocationPermissionActivity::class.java.simpleName
    private lateinit var binding: ActivityLocationPermissionBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null
    private lateinit var viewmodel: PermissionViewModel

    val locationPermissionList = listOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_permission)
        initView()
    }

    private fun initView() {

        viewmodel =
            ViewModelProvider(this).get<PermissionViewModel>(PermissionViewModel::class.java)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.clicklistener = this

        if (isLocationPermissionGranted()){
            val latlng = SharedPreferenceHelper.getInstance(this).getStringValue(Constants.PREF_ADDRESS_LATLNG,"").getSavedLatlng()
            if (latlng!=null){
                gotoActivity(MainActivity::class.java,needToFinish = true)
            }else
                getLastKnownLocation()
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted())
            requestPermissions(locationPermissionList.toTypedArray(), LOCATION_REQUESTCODE)
    }

    override fun onClick(p0: View?) {
        requestPermission()
    }

    // handle permission result here
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_REQUESTCODE -> {
                handlePermissionResult()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SETTINGS_LOCATION -> {
                if (isLocationPermissionGranted())
//                    startMainActivity()
                    getLastKnownLocation()
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
        else
            getLastKnownLocation()
//            startMainActivity()
    }

    private fun startMainActivity() {
        GlobalScope.launch(Dispatchers.Main) {
            if (isNetworkAvailable()){
                try{
                    val list = viewmodel.getAddressFromLocation(currentLatLng)
                    val address = list?.get(0)?.getAddressLine(0)
                    Log.e(TAG, "address = " + address)

                    currentLatLng?.saveLatlng(this@LocationPermissionActivity) // saving latlng str to preference
                    val dialogFragment = AddressInfoDialogFragment.newInstance(address)
                    dialogFragment.setListener(listener)
                    dialogFragment.show(supportFragmentManager.beginTransaction(), "")
                }catch (exception:IOException){
                    exception.printStackTrace()
                }

            }
            hideProgressDialog()

        }
    }

    // get last known location
    private fun getLastKnownLocation() {
        showProgressDialog()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                currentLatLng = location?.latitude?.let { LatLng(it, location.longitude) }
                Log.e(TAG, "location = " + location)
                if (currentLatLng != null) {
                    startMainActivity()
                } else {
                    // if last known location gives null then get location using locationupdates
                    fusedLocationClient.requestLocationUpdates(
                        createLocationRequest(),
                        mLocationCallback,
                        Looper.myLooper()
                    )
                }
            }
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(1000)
        mLocationRequest.setFastestInterval(500)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        return mLocationRequest
    }

    val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult?.lastLocation != null) {
                currentLatLng = locationResult.lastLocation.latitude.let {
                    LatLng(it, locationResult.lastLocation.longitude)
                }
                startMainActivity()
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    val listener = object : OnItemClickListener<String> {
        override fun onItemClick(view: View?, obj: String, position: Int) {
            gotoActivity(MainActivity::class.java,needToFinish = true)
        }
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }
}
