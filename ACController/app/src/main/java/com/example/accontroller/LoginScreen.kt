package com.example.accontroller


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import org.w3c.dom.Text
import com.android.volley.toolbox.JsonObjectRequest


class LoginScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIsAuthenticated() // todo create separate loading screen while authenticating
        setContentView(R.layout.activity_login_screen)

        val changeState = findViewById<Button>(R.id.submit)
        changeState.setOnClickListener {
           authenticate()
        }
    }

    /**
     * checks user against database to see if they have rights to access this app
     *
     */
    fun getIsAuthenticated(): Boolean {
        var isAuthorized = false

        // Instantiate the RequestQueue.
        val request = Requests(this)

        request.makeRequest("GET", "authenticate",
            responseHandler = { response ->
                if (response.has("authenticated") && response["authenticated"] == true) {
                    isAuthorized = true
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else if (response.has("error")) {
                    val errorText = response["error"].toString()
                    val errorMsg = Toast.makeText(
                        this,
                        "Error: " + errorText,
                        Toast.LENGTH_SHORT
                    )
                    errorMsg.show()
                }
            }, errorResponse = { error ->
                val errorMsg =
                    Toast.makeText(this, "Error: request not received\n" + error, Toast.LENGTH_LONG)
                errorMsg.show()
            })
        return isAuthorized
    }

    /**
     *
     */
    fun authenticate(): Boolean {
        var isAuthorized = false
        val user = findViewById<TextView>(R.id.username_field).text.toString()
        val pwrd = findViewById<TextView>(R.id.pwrd_field).text.toString()

        val params = HashMap<String, String>()
        params["username"] = user
        params["password"] = pwrd
        val body = JSONObject(params.toMap())

        val request = Requests(this)

        request.makeRequest("POST", "authenticate", Pair("auth", "true"), body,
            {response ->
                if (response.has("authenticated") && response["authenticated"] == true) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val errorMsg = Toast.makeText(
                        this,
                        "Error: " + response["error"].toString(),
                        Toast.LENGTH_SHORT
                    )
                    errorMsg.show()
                }
        },
            {error ->
                val errorMsg =
                    Toast.makeText(this, "Error reading response from API\n" + error, Toast.LENGTH_SHORT)
                errorMsg.show()
            })
        return isAuthorized
    }

}
