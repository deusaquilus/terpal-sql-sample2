package com.example.project

import com.example.project.cache.Database
import com.example.project.cache.DatabaseDriverFactory
import com.example.project.network.SpaceXApi

class SpaceXSDK(databaseDriverFactory: DatabaseDriverFactory, val api: SpaceXApi) {
  private val database = Database(databaseDriverFactory)

  @Throws(Exception::class)
  suspend fun getLaunches(forceReload: Boolean): List<RocketLaunch> {
    val cachedLaunches = database.getAllLaunches()
    println("=============== GOT HERE ===============")
    return if (cachedLaunches.isNotEmpty() && !forceReload) {
      println("-------- Getting all lauches from Database --------")
      cachedLaunches
    } else {
      println("-------- Getting all lauches from API --------")
      val allLaunches = api.getAllLaunches()
      println("-------- Printing All Launches: --------\n${allLaunches}")
      allLaunches.also {
        database.clearAndCreateLaunches(it)
      }
    }
  }
}