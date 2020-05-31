package com.demo.covid19_dashboard.viewmodels

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.covid19_dashboard.BaseApplication
import com.demo.covid19_dashboard.models.StatewiseDataResponseModel
import com.google.android.gms.maps.model.LatLng

class MainViewModel : ViewModel() {

    lateinit var statewiseLivedata: MutableLiveData<StatewiseDataResponseModel>

    // call statewise data api
    fun getStateWiseData() {
        statewiseLivedata = MainRepository.getInstance().getStateWiseData()
    }
}
