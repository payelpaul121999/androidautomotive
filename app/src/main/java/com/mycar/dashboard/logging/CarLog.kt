package com.mycar.dashboard.logging

import android.util.Log

/**
 * Stable Logcat tags so you can filter a full request later:
 *   adb logcat -s CAR_APP:D CAR_FAKE_REPO:D
 *
 * Real stack tags you will see in later phases (not used yet):
 *   CAR_API, CAR_SERVICE, VHAL, VEHICLE_SIM
 */
object CarLog {
    fun app(message: String) = Log.d("CAR_APP", message)
    fun fakeRepo(message: String) = Log.d("CAR_FAKE_REPO", message)
}
