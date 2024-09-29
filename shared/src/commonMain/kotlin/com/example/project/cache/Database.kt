package com.example.project.cache

import com.example.project.Links
import com.example.project.Patch
import com.example.project.RocketLaunch
import io.exoquery.sql.Query
import io.exoquery.sql.Sql
import io.exoquery.sql.TerpalDriver
import io.exoquery.sql.runOn
import io.exoquery.sql.sqlite.CallAfterVersion
import io.exoquery.sql.sqlite.TerpalSchema

object LaunchSchema: TerpalSchema<Unit> {
    override val version: Long
        get() = 1

    override suspend fun create(driver: TerpalDriver) {
        Sql("""
            CREATE TABLE Launch (
                flightNumber INTEGER NOT NULL,
                missionName TEXT NOT NULL,
                details TEXT,
                launchSuccess INTEGER DEFAULT NULL,
                launchDateUTC TEXT NOT NULL,
                patchUrlSmall TEXT,
                patchUrlLarge TEXT,
                articleUrl TEXT
            )
        """).action().runOn(driver)
    }

    override suspend fun migrate(driver: TerpalDriver, oldVersion: Long, newVersion: Long, vararg callbacks: CallAfterVersion) {
        // No-op
    }
}

object DAO {
    fun selectAllLaunchesInfo(): Query<RocketLaunch> {
        println("------------ BEFORE QUERY MAKE")
        val query = Sql("SELECT * FROM Launch").queryOf<RocketLaunch>()
        println("------------ AFTER QUERY MAKE")
        return query
    }
    fun removeAllLaunches() =
        Sql("DELETE FROM Launch").action()
    fun insertLaunch(rl: RocketLaunch) =
        Sql("""
           INSERT INTO Launch (
             flightNumber, missionName, details, launchSuccess, launchDateUTC, patchUrlSmall, patchUrlLarge, articleUrl) 
           VALUES 
             (${rl.flightNumber}, ${rl.missionName}, ${rl.details}, ${rl.launchSuccess}, ${rl.launchDateUTC}, ${rl.links.patch?.small}, ${rl.links.patch?.large}, ${rl.links.article}) 
        """)


}

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = databaseDriverFactory.createTerpalDriver()

    internal suspend fun getAllLaunches(): List<RocketLaunch> {
        println("--------------- Getting All Launches ---------------")
        val queryMake = DAO.selectAllLaunchesInfo()
        println("----------- BEFORE QUERY EXECUTE: ${queryMake} ------------")
        val output = queryMake.runOn(database)
        println("--------------- All Launches ---------------:\n${output}")
        return output
    }

    private fun mapLaunchSelecting(
        flightNumber: Long,
        missionName: String,
        details: String?,
        launchSuccess: Boolean?,
        launchDateUTC: String,
        patchUrlSmall: String?,
        patchUrlLarge: String?,
        articleUrl: String?
    ): RocketLaunch {
        return RocketLaunch(
            flightNumber = flightNumber.toInt(),
            missionName = missionName,
            details = details,
            launchDateUTC = launchDateUTC,
            launchSuccess = launchSuccess,
            links = Links(
                patch = Patch(
                    small = patchUrlSmall,
                    large = patchUrlLarge
                ),
                article = articleUrl
            )
        )
    }

    internal suspend fun clearAndCreateLaunches(launches: List<RocketLaunch>) {
        database.transaction {
            DAO.removeAllLaunches().run()
            launches.forEach { launch ->
                DAO.insertLaunch(launch).action().run()
            }
        }
    }
}
