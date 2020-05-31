package com.demo.covid19_dashboard.api

import com.demo.covid19_dashboard.models.StatewiseDataResponseModel
import retrofit2.Call
import retrofit2.http.*

interface ApiCallInterface {

    // api for statewise data
    @GET(NetworkHelper.API_DATA)
    fun getStatewiseData(): Call<StatewiseDataResponseModel>
}