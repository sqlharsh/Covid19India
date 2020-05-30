package com.demo.covid19_dashboard

import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.demo.covid19_dashboard.databinding.ActivityMainBinding
import com.demo.covid19_dashboard.receiver.GeofenceEventReceiver
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class MainActivity : BaseActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatLng: LatLng? = null
    private lateinit var binding:ActivityMainBinding
    private var mGeofencingClient: GeofencingClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        initView()
    }

    private fun initView(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mGeofencingClient = LocationServices.getGeofencingClient(this)
        getLastKnownLocation()
        showProgressDialog()
        Handler().postDelayed(Runnable {
            hideProgressDialog()
        },10000)
    }

    // get last known location
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                currentLatLng = location?.latitude?.let { LatLng(it, location.longitude) }
                Log.e(TAG, "location = " + location)
                if (currentLatLng != null) {
                    addGeofence()
                }else {
                    // if last known location gives null then get location using locationupdates
                    fusedLocationClient.requestLocationUpdates(
                        createLocationRequest(),
                        mLocationCallback,
                        Looper.myLooper()
                    )
                }
            }
    }
    private fun addGeofence(){
        val geofence = createGeofence()

        val geofenceRequest = geofence?.let { createGeofenceRequest(it) }
        val intent = Intent(this, GeofenceEventReceiver::class.java)
        val pendingIntet =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        mGeofencingClient?.addGeofences(geofenceRequest,pendingIntet)
            ?.addOnCompleteListener {
                Log.e(TAG,"geofence Added !!")
            }
            ?.addOnFailureListener{
                Log.e(TAG,"something went wrong !")
                it.printStackTrace()
            }
    }

    // Create a Geofence
    private fun createGeofence() = currentLatLng?.latitude?.let {
            currentLatLng?.longitude?.let { it1 ->
                Geofence.Builder()
                    .setRequestId("My Geofence")
                    .setCircularRegion(it, it1, 100f)
                    .setExpirationDuration(60 * 60 * 1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            }
        }

    // Create a Geofence Request
    private fun createGeofenceRequest(geofence: Geofence) = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofence(geofence)
            .build()

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest():LocationRequest {
       val mLocationRequest = LocationRequest()
        mLocationRequest.setInterval(1000)
        mLocationRequest.setFastestInterval(500)
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        return mLocationRequest
    }

    val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult?.lastLocation!=null){
                currentLatLng = locationResult.lastLocation.latitude.let {
                    LatLng(it, locationResult.lastLocation.longitude) }

                addGeofence()
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

}
