package com.android.tripbook.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.tripbook.data.local.converter.DataConverters
import com.android.tripbook.data.local.dao.*
import com.android.tripbook.data.local.entity.*
import com.android.tripbook.data.local.migration.DatabaseMigrations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Main Room database class for the TripBook app
 */
@Database(
    entities = [
        PostEntity::class,
        UserEntity::class,
        CommentEntity::class,
        TravelAgencyEntity::class,
        UserPostLikeEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DataConverters::class)
abstract class TripBookDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun travelAgencyDao(): TravelAgencyDao
    abstract fun userPostLikeDao(): UserPostLikeDao

    companion object {
        @Volatile
        private var INSTANCE: TripBookDatabase? = null

        fun getInstance(context: Context): TripBookDatabase {
            return INSTANCE ?: synchronized(this) {                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripBookDatabase::class.java,
                    "tripbook_database"
                )
                    .addCallback(DatabaseCallback())
                    // Add migrations for future schema updates
                    // For now, we're at version 1, but this prepares us for the future
                    // .addMigrations(DatabaseMigrations.MIGRATION_1_2)
                    // Fallback strategy for migrations that aren't explicitly defined
                    // Only enable this in development, not in production
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDatabase(database)
                    }
                }
            }
        }

        /**
         * Pre-populate the database with some sample data
         */
        private suspend fun prepopulateDatabase(database: TripBookDatabase) {
            val userDao = database.userDao()
            val currentDate = Date()

            // Add a sample user
            val userId = "user1"
            val user = UserEntity(
                id = userId,
                username = "johndoe",
                email = "john.doe@example.com",
                displayName = "John Doe",
                profileImage = null,
                bio = "Travel enthusiast and photographer",
                createdAt = currentDate,
                updatedAt = currentDate
            )
            userDao.insertUser(user)

            // Add a sample agency
            val agencyDao = database.travelAgencyDao()
            val agencyId = "agency1"
            val agency = TravelAgencyEntity(
                id = agencyId,
                name = "Adventure Tours",
                description = "Specialized in adventure travel experiences",
                logo = null,
                website = "https://example.com",
                contactPhone = "+1234567890",
                contactEmail = "info@adventuretours.example.com",
                createdAt = currentDate,
                updatedAt = currentDate
            )
            agencyDao.insertTravelAgency(agency)

            // Add sample posts
            val postDao = database.postDao()
            for (i in 1..5) {
                val postId = "post$i"
                val post = PostEntity(
                    id = postId,
                    userId = userId,
                    title = "Sample Post $i",
                    description = "This is a sample travel post description for testing purposes.",
                    images = listOf("https://example.com/image$i.jpg"),
                    location = "Location $i",
                    latitude = if (i % 2 == 0) 40.7128 else null,
                    longitude = if (i % 2 == 0) -74.0060 else null,
                    tags = listOf("travel", "sample", "tag$i"),
                    agencyId = if (i % 2 == 0) agencyId else null,
                    likes = i * 10,
                    comments = i * 2,
                    createdAt = Date(currentDate.time - i * 86400000), // Older by i days
                    updatedAt = Date(currentDate.time - i * 86400000)
                )
                postDao.insertPost(post)

                // Add some comments to the post
                val commentDao = database.commentDao()
                for (j in 1..i) {
                    val comment = CommentEntity(
                        id = "comment_${postId}_$j",
                        postId = postId,
                        userId = userId,
                        content = "This is comment #$j on post #$i",
                        createdAt = Date(currentDate.time - (i * 1000)),
                        updatedAt = Date(currentDate.time - (i * 1000))
                    )
                    commentDao.insertComment(comment)
                }
            }
        }
    }
}
