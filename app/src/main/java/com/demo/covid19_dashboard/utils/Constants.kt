package com.demo.covid19_dashboard.utils

import android.Manifest

class Constants {
    companion object {

        const val SUCCESS = 1
        const val FAILURE = 0

        const val REQUEST_CHECK_LOCATION_SETTINGS = 1009
        const val LOCATION_REQUESTCODE = 1010
        const val REQUEST_CODE_SETTINGS_LOCATION = 1011
        const val PUSH_NOTIFICATION_CHANNEL_ID = "COVID_REMINDER"

        const val PREF_ADDRESS_LATLNG = "LATLNG"

        const val ITEM = 0
        const val HEADER = 1
    }
}