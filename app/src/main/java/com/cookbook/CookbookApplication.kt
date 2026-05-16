package com.cookbook

import android.app.Application
import com.cookbook.backend.FirebaseManager

class CookbookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseManager.connect(this)
    }
}
