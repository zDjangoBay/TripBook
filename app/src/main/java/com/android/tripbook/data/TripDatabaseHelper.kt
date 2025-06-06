package com.android.tripbook.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.Calendar
import java.util.Date

class TripDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tripbook.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_TRIPS = "trips"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESTINATION = "destination"
        private const val COLUMN_START_DATE = "start_date"
        private const val COLUMN_END_DATE = "end_date"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_AGENCY = "agency"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_TRIPS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESTINATION TEXT NOT NULL,
                $COLUMN_START_DATE INTEGER NOT NULL,
                $COLUMN_END_DATE INTEGER NOT NULL,
                $COLUMN_DURATION INTEGER NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_AGENCY TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRIPS")
        onCreate(db)
    }

    fun insertTrip(trip: Trip): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, trip.title)
            put(COLUMN_DESTINATION, trip.destination)
            put(COLUMN_START_DATE, trip.startDate.time)
            put(COLUMN_END_DATE, trip.endDate.time)
            put(COLUMN_DURATION, trip.duration)
            put(COLUMN_TYPE, trip.type)
            put(COLUMN_DESCRIPTION, trip.description)
            put(COLUMN_AGENCY, trip.agency)
        }
        return db.insert(TABLE_TRIPS, null, values)
    }

    fun getTripsByDate(date: Date): List<Trip> {
        val trips = mutableListOf<Trip>()
        val db = readableDatabase

        // Get trips that start on the selected date
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        val cursor = db.query(
            TABLE_TRIPS,
            null,
            "$COLUMN_START_DATE >= ? AND $COLUMN_START_DATE <= ?",
            arrayOf(startOfDay.toString(), endOfDay.toString()),
            null,
            null,
            "$COLUMN_START_DATE ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val trip = Trip(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    destination = it.getString(it.getColumnIndexOrThrow(COLUMN_DESTINATION)),
                    startDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_START_DATE))),
                    endDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_END_DATE))),
                    duration = it.getInt(it.getColumnIndexOrThrow(COLUMN_DURATION)),
                    type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    agency = it.getString(it.getColumnIndexOrThrow(COLUMN_AGENCY))
                )
                trips.add(trip)
            }
        }

        return trips
    }

    fun getAllTrips(): List<Trip> {
        val trips = mutableListOf<Trip>()
        val db = readableDatabase

        val cursor = db.query(TABLE_TRIPS, null, null, null, null, null, "$COLUMN_START_DATE ASC")

        cursor.use {
            while (it.moveToNext()) {
                val trip = Trip(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    destination = it.getString(it.getColumnIndexOrThrow(COLUMN_DESTINATION)),
                    startDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_START_DATE))),
                    endDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_END_DATE))),
                    duration = it.getInt(it.getColumnIndexOrThrow(COLUMN_DURATION)),
                    type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    agency = it.getString(it.getColumnIndexOrThrow(COLUMN_AGENCY))
                )
                trips.add(trip)
            }
        }

        return trips
    }

    fun getTripsByType(type: String): List<Trip> {
        val trips = mutableListOf<Trip>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_TRIPS,
            null,
            "$COLUMN_TYPE = ?",
            arrayOf(type),
            null,
            null,
            "$COLUMN_START_DATE ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val trip = Trip(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    destination = it.getString(it.getColumnIndexOrThrow(COLUMN_DESTINATION)),
                    startDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_START_DATE))),
                    endDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_END_DATE))),
                    duration = it.getInt(it.getColumnIndexOrThrow(COLUMN_DURATION)),
                    type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    agency = it.getString(it.getColumnIndexOrThrow(COLUMN_AGENCY))
                )
                trips.add(trip)
            }
        }

        return trips
    }

    fun getTripsByDuration(duration: Int): List<Trip> {
        val trips = mutableListOf<Trip>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_TRIPS,
            null,
            "$COLUMN_DURATION = ?",
            arrayOf(duration.toString()),
            null,
            null,
            "$COLUMN_START_DATE ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                val trip = Trip(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    title = it.getString(it.getColumnIndexOrThrow(COLUMN_TITLE)),
                    destination = it.getString(it.getColumnIndexOrThrow(COLUMN_DESTINATION)),
                    startDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_START_DATE))),
                    endDate = Date(it.getLong(it.getColumnIndexOrThrow(COLUMN_END_DATE))),
                    duration = it.getInt(it.getColumnIndexOrThrow(COLUMN_DURATION)),
                    type = it.getString(it.getColumnIndexOrThrow(COLUMN_TYPE)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    agency = it.getString(it.getColumnIndexOrThrow(COLUMN_AGENCY))
                )
                trips.add(trip)
            }
        }

        return trips
    }
}
