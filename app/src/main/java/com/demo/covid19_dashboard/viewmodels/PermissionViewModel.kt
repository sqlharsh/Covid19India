package com.demo.covid19_dashboard.viewmodels

import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import com.demo.covid19_dashboard.BaseApplication
import com.google.android.gms.maps.model.LatLng

class PermissionViewModel : ViewModel() {

    // Get address from latlng using reverse geocoder
    @WorkerThread
    suspend fun getAddressFromLocation(latlng:LatLng?):List<Address>? {
        val geocoder = Geocoder(BaseApplication.getApplicationContext())
        val list = latlng?.latitude?.let { geocoder.getFromLocation(it,latlng.longitude,1) }
        Log.e("tag","address in scope = "+list?.get(0)?.getAddressLine(0))
        return list
    }
}
