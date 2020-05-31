package com.demo.covid19_dashboard.models


import com.google.gson.annotations.SerializedName

data class StatewiseDataResponseModel(
    @SerializedName("statewise")
    val statewise: List<Statewise>
)