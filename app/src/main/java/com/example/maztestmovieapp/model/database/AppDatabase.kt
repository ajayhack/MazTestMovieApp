package com.bonushub.pax.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.maztestmovieapp.model.database.*


/**
 * =========Written By Ajay Thakur (18th Nov 2020)==========
 **/

@Database(
    entities = [Results::class],
    version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    //region==============Accessing Dao for further uses:-
    abstract fun dao(): AppDao?
    //endregion

    companion object {
        private var dbInstance: AppDatabase? = null
        private var dbName: String = "HdfcDB"

        @Synchronized
        fun getDatabase(context: Context): AppDatabase? {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
                    .addMigrations(MIGRATION_1_2)
                    .build()
            }
            return dbInstance
        }

        //region===============================Below method is to clean up DB Instance:-
        fun cleanUpDB() {
            dbInstance = null
        }
        //endregion

        //region================================Room DB Migration Query Should be write below:-
        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
        //endregion
    }
}