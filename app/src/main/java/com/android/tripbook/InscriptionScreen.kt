import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.sql.Connection
import java.sql.DriverManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { InscriptionScreen() }
    }
}

@Composable
fun InscriptionScreen() {
    var nom by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var sexe by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var paysOrigine by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Créer un Profil", fontSize = 24.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom") })
        TextField(value = adresse, onValueChange = { adresse = it }, label = { Text("Adresse") })
        TextField(value = sexe, onValueChange = { sexe = it }, label = { Text("Sexe") })
        TextField(value = destination, onValueChange = { destination = it }, label = { Text("Destination") })
        TextField(value = paysOrigine, onValueChange = { paysOrigine = it }, label = { Text("Pays d'origine") })
        TextField(
            value = age.toString(),
            onValueChange = { age = it.toIntOrNull() ?: 0 },
            label = { Text("Âge") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { enregistrerUtilisateur(nom, adresse, sexe, destination, paysOrigine, age) }) {
            Text("S'inscrire")
        }

    }
    }
}

// Connexion à PostgreSQL
object DatabaseConnection {
    private const val URL = "jdbc:postgresql://localhost:5432/posts"
    private const val USER = "postgres"
    private const val PASSWORD = "ton_mot_de_passe"

    fun connect(): Connection? {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: Exception) {
            println("Erreur de connexion : ${e.message}")
            null
        }
    }
}

// Fonction d'enregistrement de l'utilisateur
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