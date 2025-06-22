package com.android.tripbook

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tohpoh.models.Post
import com.example.tohpoh.models.User

class DataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "ondjoss.db"
        const val DATABASE_VERSION = 2

        // Tables
        const val TABLE_USERS = "Users"
        const val TABLE_POSTS = "Posts"
        const val TABLE_COMMENTS = "Comments"
        const val TABLE_LIKES = "Likes"
        const val TABLE_SHARES = "Shares"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                email TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                profile_picture TEXT DEFAULT NULL,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP
            );
        """.trimIndent()

        val createPostsTable = """
    CREATE TABLE IF NOT EXISTS $TABLE_POSTS (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER NOT NULL,
        content TEXT NOT NULL,
        image TEXT DEFAULT NULL,
        created_at TEXT DEFAULT CURRENT_TIMESTAMP,
        like_count INTEGER DEFAULT 0,       
        comment_count INTEGER DEFAULT 0,    
        share_count INTEGER DEFAULT 0,    
        privacy TEXT DEFAULT 'Public',     
        FOREIGN KEY(user_id) REFERENCES $TABLE_USERS(id)
    );
""".trimIndent()

        val createCommentsTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_COMMENTS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                post_id INTEGER NOT NULL,
                parent_comment_id INTEGER DEFAULT NULL,
                content TEXT NOT NULL,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(user_id) REFERENCES $TABLE_USERS(id),
                FOREIGN KEY(post_id) REFERENCES $TABLE_POSTS(id)
            );
        """.trimIndent()

        val createLikesTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_LIKES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                post_id INTEGER DEFAULT NULL,
                comment_id INTEGER DEFAULT NULL,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(user_id) REFERENCES $TABLE_USERS(id),
                FOREIGN KEY(post_id) REFERENCES $TABLE_POSTS(id),
                FOREIGN KEY(comment_id) REFERENCES $TABLE_COMMENTS(id)
            );
        """.trimIndent()

        val createSharesTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_SHARES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                post_id INTEGER NOT NULL,
                created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(user_id) REFERENCES $TABLE_USERS(id),
                FOREIGN KEY(post_id) REFERENCES $TABLE_POSTS(id)
            );
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createPostsTable)
        db.execSQL(createCommentsTable)
        db.execSQL(createLikesTable)
        db.execSQL(createSharesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_POSTS ADD COLUMN like_count INTEGER DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_POSTS ADD COLUMN comment_count INTEGER DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_POSTS ADD COLUMN share_count INTEGER DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_POSTS ADD COLUMN privacy TEXT DEFAULT 'Public'")
        }
    }

    fun getUserByEmail(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Users WHERE email = ? AND password = ?", arrayOf(email, password))

        return if (cursor.moveToFirst()) {
            User(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("username")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("profile_picture")),
                cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            )
        } else {
            null
        }.also { cursor.close(); db.close() }
    }
    fun registerUser(username: String, email: String, password: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("email", email)
            put("password", password) // À remplacer par un hash sécurisé
        }

        return try {
            db.insertOrThrow("Users", null, values) != -1L
        } catch (e: SQLiteConstraintException) {
            false // Username ou Email déjà pris
        } finally {
            db.close()
        }
    }

    fun getUserId(email: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id FROM Users WHERE email = ?", arrayOf(email))
        return if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("id")) else -1
    }
    fun addPost(post: Post): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", post.userId)
            put("content", post.content)
            post.imageUrl?.let { put("image", it) }
            put("created_at", post.createdAt)
            put("like_count", post.likeCount)       // Nom corrigé
            put("comment_count", post.commentCount) // Nom corrigé
            put("share_count", post.shareCount)     // Nom corrigé
            put("privacy", post.privacy)
        }

        return try {
            db.insertOrThrow(TABLE_POSTS, null, values) != -1L
        } catch (e: SQLiteConstraintException) {
            false
        } finally {
            db.close()
        }
    }


    fun getAllPosts(): List<Post> {
        val db = readableDatabase
        val postList = mutableListOf<Post>()
        val cursor = db.rawQuery("SELECT * FROM Posts ORDER BY created_at DESC", null)

        while (cursor.moveToNext()) {
            val post = Post(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                cursor.getString(cursor.getColumnIndexOrThrow("content")),
                cursor.getString(cursor.getColumnIndexOrThrow("image")),
                cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                cursor.getInt(cursor.getColumnIndexOrThrow("likeCount")),
                cursor.getInt(cursor.getColumnIndexOrThrow("commentCount")),
                cursor.getInt(cursor.getColumnIndexOrThrow("shareCount")),
                cursor.getString(cursor.getColumnIndexOrThrow("privacy")),
                null // À remplacer par l'utilisateur correspondant
            )
            postList.add(post)
        }

        cursor.close()
        db.close()
        return postList
    }

    fun addPost(userId: Int, content: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("content", content)
        }

        return try {
            db.insertOrThrow("Posts", null, values) != -1L
        } catch (e: SQLiteConstraintException) {
            false // Erreur lors de l'insertion
        } finally {
            db.close()
        }
    }

    fun likePost(userId: Int, postId: Int): Boolean {
        val db = writableDatabase

        // Vérifier si l'utilisateur a déjà liké ce post
        val cursor = db.rawQuery("SELECT * FROM Likes WHERE user_id = ? AND post_id = ?", arrayOf(userId.toString(), postId.toString()))
        val alreadyLiked = cursor.count > 0
        cursor.close()

        return try {
            if (alreadyLiked) {
                // Supprimer le like (dislike)
                db.delete("Likes", "user_id = ? AND post_id = ?", arrayOf(userId.toString(), postId.toString()))
                db.execSQL("UPDATE Posts SET likeCount = likeCount - 1 WHERE id = ?", arrayOf(postId.toString()))
            } else {
                // Ajouter un like
                val values = ContentValues().apply {
                    put("user_id", userId)
                    put("post_id", postId)
                }
                db.insert("Likes", null, values)
                db.execSQL("UPDATE Posts SET likeCount = likeCount + 1 WHERE id = ?", arrayOf(postId.toString()))
            }
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun commentOnPost(userId: Int, postId: Int, content: String): Boolean {
        val db = writableDatabase

        return try {
            val values = ContentValues().apply {
                put("user_id", userId)
                put("post_id", postId)
                put("content", content)
            }
            db.insert("Comments", null, values)
            db.execSQL("UPDATE Posts SET commentCount = commentCount + 1 WHERE id = ?", arrayOf(postId.toString()))
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun sharePost(userId: Int, postId: Int): Boolean {
        val db = writableDatabase

        // Vérifier si l'utilisateur a déjà partagé ce post
        val cursor = db.rawQuery("SELECT * FROM Shares WHERE user_id = ? AND post_id = ?", arrayOf(userId.toString(), postId.toString()))
        val alreadyShared = cursor.count > 0
        cursor.close()

        return try {
            if (!alreadyShared) {
                val values = ContentValues().apply {
                    put("user_id", userId)
                    put("post_id", postId)
                }
                db.insert("Shares", null, values)
                db.execSQL("UPDATE Posts SET shareCount = shareCount + 1 WHERE id = ?", arrayOf(postId.toString()))
            }
            true
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }
    fun getUserById(id: Int): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USERS WHERE id = ?", arrayOf(id.toString()))
        val user: User? = if (cursor.moveToFirst()) {
            User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                profilePicture = cursor.getString(cursor.getColumnIndexOrThrow("profile_picture")),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
            )
        } else {
            null
        }
        cursor.close()
        db.close()
        return user
    }



}
