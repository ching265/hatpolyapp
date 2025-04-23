package com.example.lovepoly.data

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("LovePolyPrefs", Context.MODE_PRIVATE)

    fun saveRotation(level: Int, rotX: Float, rotY: Float) {
        prefs.edit()
            .putFloat("level_${level}_rotX", rotX)
            .putFloat("level_${level}_rotY", rotY)
            .apply()
    }

    fun getRotationX(level: Int): Float = prefs.getFloat("level_${level}_rotX", 0f)

    fun getRotationY(level: Int): Float = prefs.getFloat("level_${level}_rotY", 0f)

    fun saveSpeed(speed: Int) {
        prefs.edit().putInt("rotate_speed", speed).apply()
    }

    fun getSpeed(): Int = prefs.getInt("rotate_speed", 5)
}