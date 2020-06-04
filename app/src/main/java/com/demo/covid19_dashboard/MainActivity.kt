package com.demo.covid19_dashboard

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.demo.covid19_dashboard.adapter.BannerAdapter
import com.demo.covid19_dashboard.adapter.StateDataAdapter
import com.demo.covid19_dashboard.databinding.ActivityMainBinding
import com.demo.covid19_dashboard.receiver.GeofenceEventReceiver
import com.demo.covid19_dashboard.utils.Constants
import com.demo.covid19_dashboard.utils.getSavedLatlng
import com.demo.covid19_dashboard.viewmodels.MainViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.iMpactHealth.iwire.solutions.utils.SharedPreferenceHelper
import kotlinx.coroutines.handleCoroutineException
import java.util.*
import kotlin.concurrent.timer

class MainActivity : BaseActivity(), ViewPager.OnPageChangeListener {

    private val TAG = MainActivity::class.java.simpleName
    private var currentLatLng: LatLng? = null
    private lateinit var binding:ActivityMainBinding
    private var mGeofencingClient: GeofencingClient? = null
    private lateinit var viewmodel: MainViewModel
    private lateinit var adapter: StateDataAdapter
    private lateinit var timer: Timer
    private lateinit var handler:Handler
    private var currentPage = 0;
    private val DELAY_MS: Long = 2000 //delay in milliseconds before task is to be executed
    private val PERIOD_MS: Long = 5000 // time in milliseconds between successive task executions.


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
        binding.rcvStatedata.layoutManager = LinearLayoutManager(this)
        adapter = StateDataAdapter(this)
        binding.rcvStatedata.adapter = adapter
        if (!SharedPreferenceHelper.getInstance(this).getBoolanValue(Constants.PREF_IS_GEOFENCE_ADDED,false))
            addGeofence()

        // setting up image slider and auto scroll functionality
        val listImages = arrayListOf<Int>(R.drawable.ic_social_distancing,R.drawable.ic_hand_sanatize,R.drawable.ic_disinfect_virus)
        val adapter = BannerAdapter(this,listImages)
        binding.sliderViewPager.adapter = adapter
        binding.circlePagerIndicator.setViewPager(binding.sliderViewPager)

        handler = Handler()
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(sliderRunnable)
            }
        },DELAY_MS,PERIOD_MS)

        binding.sliderViewPager.addOnPageChangeListener(this)
    }

    private fun setUpViewModel(){
        viewmodel = ViewModelProvider(this).get<MainViewModel>(MainViewModel::class.java)
        Handler().postDelayed(Runnable {
            getCovidData()
        },1000) // this delay is just to show progress animation
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
                SharedPreferenceHelper.getInstance(this).setValue(Constants.PREF_IS_GEOFENCE_ADDED,true)
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
                if (it!=null){
                    val list = it.statewise
                    list.get(0).type = Constants.HEADER
                    adapter.addAllData(it.statewise)
                }
                hideProgressDialog()
            })
        }
    }

    val sliderRunnable = object : Runnable {
        override fun run() {
            if (currentPage == 3) {
                currentPage = 0;
            }
            binding.sliderViewPager.setCurrentItem(currentPage++,true)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        handler.removeCallbacks(sliderRunnable)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        currentPage = position
    }

    override fun onPageSelected(position: Int) {

    }

}
