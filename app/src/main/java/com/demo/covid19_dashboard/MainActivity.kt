package com.demo.covid19_dashboard

import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.demo.covid19_dashboard.databinding.ActivityMainBinding
import com.demo.covid19_dashboard.receiver.GeofenceEventReceiver
import com.demo.covid19_dashboard.utils.Constants
import com.demo.covid19_dashboard.utils.getSavedLatlng
import com.demo.covid19_dashboard.viewmodels.MainViewModel
import com.demo.covid19_dashboard.viewmodels.PermissionViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.iMpactHealth.iwire.solutions.utils.SharedPreferenceHelper

class MainActivity : BaseActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private var currentLatLng: LatLng? = null
    private lateinit var binding:ActivityMainBinding
    private var mGeofencingClient: GeofencingClient? = null
    private lateinit var viewmodel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        initView()
        setUpViewModel()
    }

    private fun initView(){

        showProgressDialog()
        mGeofencingClient = LocationServices.getGeofencingClient(this)
        currentLatLng = SharedPreferenceHelper.getInstance(this).getStringValue(Constants.PREF_ADDRESS_LATLNG,"").getSavedLatlng()

        addGeofence()
    }

    private fun setUpViewModel(){
        viewmodel = ViewModelProvider(this).get<MainViewModel>(MainViewModel::class.java)
        Handler().postDelayed(Runnable {
            getCovidData()
        },1000)

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

    //
    private fun getCovidData() {
        if (isNetworkAvailable()) {
            viewmodel.getStateWiseData()
            viewmodel.statewiseLivedata.observe(this, Observer {
                hideProgressDialog()
                if (it!=null){

                }
            })

        }
    }

    override fun onStop() {
        super.onStop()
    }

}
