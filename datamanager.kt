package data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DatabaseManager {
    private const val DB_URL = "jdbc:sqlite:password_recovery.db"
    
    fun getConnection(): Connection {
        return DriverManager.getConnection(DB_URL)
    }
    
    fun initDatabase() {
        try {
            getConnection().use { conn ->
                // Create users table
                conn.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        email TEXT UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """)
                
                // Create reset_tokens table
                conn.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS reset_tokens (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER,
                        token TEXT UNIQUE NOT NULL,
                        expires_at TIMESTAMP NOT NULL,
                        used BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users (id)
                    )
                """)
                
                // Insert demo user if not exists
                val stmt = conn.prepareStatement("""
                    INSERT OR IGNORE INTO users (email, password_hash) 
                    VALUES (?, ?)
                """)
                stmt.setString(1, "demo@example.com")
                stmt.setString(2, "salt123:hashedpassword123") // Demo password: "password123"
                stmt.executeUpdate()
                
                println("Database initialized successfully")
            }
        } catch (e: SQLException) {
            println("Error initializing database: ${e.message}")
        }
    }
}