package com.example.testproject

import android.app.Application
import com.onesignal.OneSignal


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }

}