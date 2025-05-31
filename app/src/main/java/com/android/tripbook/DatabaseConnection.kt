package com.android.tripbook

import java.sql.Connection
import java.sql.DriverManager

object DatabaseConnection {
    private const val URL = "jdbc:postgresql://localhost:5432/posts" // Nom de ta BD
    private const val USER = "postgres" // Ton utilisateur PostgreSQL
    private const val PASSWORD = "ton_mot_de_passe" // Remplace par ton vrai mot de passe

    fun connect(): Connection? {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: Exception) {
            println("Erreur de connexion : ${e.message}")
            null
        }
    }
}

fun enregistrerUtilisateur(nom: String, adresse: String, sexe: String, destination: String, paysOrigine: String, age: Int) {
    val connection = DatabaseConnection.connect()

    connection?.use {
        val statement = it.prepareStatement(
            "INSERT INTO utilisateur (nom, adresse, sexe, destination, pays_d_origine, age) VALUES (?, ?, ?, ?, ?, ?)"
        )
        statement.setString(1, nom)
        statement.setString(2, adresse)
        statement.setString(3, sexe)
        statement.setString(4, destination)
        statement.setString(5, paysOrigine)
        statement.setInt(6, age)

        statement.executeUpdate()
        println("Utilisateur enregistré avec succès !")
    }
}