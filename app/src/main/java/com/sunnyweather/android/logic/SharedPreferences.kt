package com.sunnyweather.android.logic

import android.content.SharedPreferences



fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.block()
    editor.apply()
}