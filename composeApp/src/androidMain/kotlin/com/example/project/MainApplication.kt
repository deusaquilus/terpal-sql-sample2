package com.example.project

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    startKoin {
      androidContext(this@MainApplication)
      modules(appModule)
    }

    //Thread.setDefaultUncaughtExceptionHandler(object: Thread.UncaughtExceptionHandler {
    //  override fun uncaughtException(t: Thread, e: Throwable) {
    //    println("----------------- EXCEPTION HERE -----------------")
    //    e.printStackTrace()
    //    System.exit(1)
    //  }
    //})
  }



}