package com.example.project.cache

import android.content.Context
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.exoquery.sql.TerpalDriver
import io.exoquery.sql.android.TerpalAndroidDriver

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun createTerpalDriver(): TerpalDriver {
        val factory = FrameworkSQLiteOpenHelperFactory()
        val openHelper = factory.create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("launch.db")
                .noBackupDirectory(true)
                .callback(AndroidSqliteDriver.Callback(AppDatabase.Schema))
                .build(),
        )
        return TerpalAndroidDriver.fromSingleOpenHelper(openHelper)
        //return TerpalAndroidDriver.fromApplicationContext("launch.db", context, LaunchSchema)
    }

}
