package com.mycar.dashboard.logging

import android.util.Log

/**
 * Structured tags for a full-stack trace later:
 * UI → framework → HAL → ECU.
 *
 * Filter:
 * adb logcat -s CAR_APP:D CAR_FAKE_REPO:D CAR_NAV:D CAR_MAP:D CAR_GPS:D CAR_UX:D CAR_POWER:D
 */
object CarLog {
    fun app(message: String) = Log.d("CAR_APP", message)
    fun fakeRepo(message: String) = Log.d("CAR_FAKE_REPO", message)
    fun nav(message: String) = Log.d("CAR_NAV", message)
    fun map(message: String) = Log.d("CAR_MAP", message)
    fun gps(message: String) = Log.d("CAR_GPS", message)
    fun ux(message: String) = Log.d("CAR_UX", message)
    fun power(message: String) = Log.d("CAR_POWER", message)
    fun vehicle(message: String) = Log.d("CAR_VEHICLE", message)
}
