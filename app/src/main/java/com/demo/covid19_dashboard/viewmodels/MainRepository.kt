package com.demo.covid19_dashboard.viewmodels

import androidx.lifecycle.MutableLiveData
import com.demo.covid19_dashboard.api.ApiHelperClass
import com.demo.covid19_dashboard.models.StatewiseDataResponseModel
import retrofit2.Call
import retrofit2.Response

class MainRepository private constructor() {

    fun getStateWiseData(): MutableLiveData<StatewiseDataResponseModel> {

        val data = MutableLiveData<StatewiseDataResponseModel>()

        ApiHelperClass.getInstance().getStatewiseData()
            .enqueue(object : retrofit2.Callback<StatewiseDataResponseModel> {
                override fun onResponse(
                    call: Call<StatewiseDataResponseModel>,
                    response: Response<StatewiseDataResponseModel>
                ) {
                    data.value = response.body()
                }

                override fun onFailure(
                    call: Call<StatewiseDataResponseModel>,
                    t: Throwable
                ) {

                    data.value = null
                }
            })

        return data
    }

    /* singleton */
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: MainRepository().also { instance = it }
            }
    }
}
