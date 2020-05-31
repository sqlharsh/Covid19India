package com.iMpactHealth.iwire.solutions.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

class SharedPreferenceHelper private constructor(protected var mContext: Context) {

    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor?

    init {
        val stringId = mContext.applicationInfo.labelRes
        pref = mContext.getSharedPreferences(mContext.getString(stringId) + "_SharedPreferences", 0)
        editor = pref.edit()
    }

    fun setValue(key: String, value: String) {
        encodeValue(key,value)
    }

    /**
     * Method to write long value to sharedpreference
     *
     * @param key get key
     * @param val get value
     */
    fun encodeValue(key: String, value: String?) {
        var encryptedValue: String = ""
        if (value!=null)
            encryptedValue = Base64.encodeToString(value.toByteArray(), Base64.DEFAULT)
        editor!!.putString(key, encryptedValue)
        editor.commit()
    }

    /**
     * Method to decode value from sharedpreference
     *
     * @param key          get key
     * @param defaultValue get default boolean Value
     * @return decoded value
     */
    fun decodeValue(key: String, defaultValue: String): String? {
        var rslt = pref.getString(key, defaultValue)
        if (rslt != null && rslt != defaultValue) {
            rslt = String(Base64.decode(rslt, Base64.DEFAULT)) //convert bytearray to String
        }
        return rslt
    }


    fun setValue(key: String, value: Int) {
        encodeValue(key,value.toString())
    }

    fun setValue(key: String, value: Double) {
        encodeValue(key,value.toString())
    }

    fun setValue(key: String, value: Long) {
        encodeValue(key,value.toString())
    }

    fun setValue(key: String, value: Float) {
        encodeValue(key,value.toString())
    }

    fun setValue(key: String, value: Boolean) {
        encodeValue(key,value.toString())
    }

    fun getStringValue(key: String, defaultValue: String): String? {
        val decodedString = decodeValue(key,defaultValue.toString())
        return decodedString
    }

    fun getIntValue(key: String, defaultValue: Int): Int {
        val decodedString = decodeValue(key,defaultValue.toString())
        return decodedString?.toInt()!!
    }

    fun getLongValue(key: String, defaultValue: Long): Long {
        val decodedString = decodeValue(key,defaultValue.toString())
        return decodedString?.toLong()!!
    }

    fun getFloatValue(key: String, defaultValue: Float): Float {
        val decodedString = decodeValue(key,defaultValue.toString())
        return decodedString?.toFloat()!!
    }

    fun getBoolanValue(key: String, defaultValue: Boolean): Boolean {
        val decodedString = decodeValue(key,defaultValue.toString())
        return decodedString?.toBoolean()!!
    }

    fun removeKey(key: String) {

        if (editor != null) {
            editor.remove(key)
            editor.commit()
        }
    }

    fun clear() {
        editor!!.clear().commit()
    }

    fun isContain(key: String): Boolean {
        return pref.contains(key)
    }

    companion object {
        private val TAG = SharedPreferenceHelper::class.java.simpleName
        private var sInstance: SharedPreferenceHelper? = null

        @Synchronized
        fun getInstance(context: Context): SharedPreferenceHelper {

            if (sInstance == null) {
                sInstance = SharedPreferenceHelper(context.applicationContext)
            }
            return sInstance as SharedPreferenceHelper
        }
    }
}


