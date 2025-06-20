/*package com.android.tripbook

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tripbook.ui.theme.TripBookTheme
Nangu
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import com.android.tripbook.restapi.ApiClient
import com.android.tripbook.restapi.ApiService
import com.android.tripbook.restapi.TokenResponse

import com.tripbook.userprofilendedilan.UserProfileNdeDilanEntryPoint
 userprofile

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
 Nangu
            TripBookTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(Modifier.padding(innerPadding))
                }
            }

            UserProfileNdeDilanEntryPoint( )
 userprofile
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                signIn(username, password, context)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Sign In")
        }
    }
}

fun signIn(username: String, password: String, context: Context) {
    val service = ApiClient.getClient().create(ApiService::class.java)
    val call = service.getToken("password", username, password)

    call.enqueue(object : Callback<TokenResponse> {
        override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
            if (response.isSuccessful) {
                val token = response.body()
                Toast.makeText(context, "Welcome ${token?.userName}", Toast.LENGTH_SHORT).show()
                // Save token.accessToken if needed
            } else {
                Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
            Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
        }
    })
}

interface APIService {
    @FormUrlEncoded
    @POST("your-login-endpoint") // Replace with actual endpoint
    fun getToken(
        @Field("grant_type") grantType: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<ResponseBody>
}

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080/" // Replace with actual base URL

    fun getClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TripBookTheme {
        LoginScreen()
    }
 Nangu
}*/

}
userprofile
