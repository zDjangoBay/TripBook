fun getDatabase(context: Context): TripBookDatabase {
    return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
            context.applicationContext,
            TripBookDatabase::class.java,
            "tripbook_database"
        )
            .addMigrations(MIGRATION_1_2) // Add your migrations here
            .build()
        INSTANCE = instance
        instance
    }
}