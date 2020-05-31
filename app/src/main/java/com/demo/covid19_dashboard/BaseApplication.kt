package com.demo.covid19_dashboard

import android.app.Application
import android.content.Context


class BaseApplication : Application() {


    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

    }

    companion object {
        private var instance: BaseApplication? = null

        fun getApplicationContext(): Context {
            return instance?.applicationContext!!
        }
    }
}