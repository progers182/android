package com.example.accontroller

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.timer

val OPTS = arrayOf(
    "Low Vent",
    "High Vent",
    "Low Cool",
    "High Cool",
    "Pump",
    "Off"
)

class MainActivity : AppCompatActivity(), AcOptsDialog.SingleChoiceListener {
    private var status: TextView? = null
    private var currStateNum: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // communicate with Arduino and fetch current state
        fetchState()

        status = findViewById(R.id.curr_state)

        // create onClickListener for change state button
        val changeState = findViewById<Button>(R.id.change_state)
        changeState.setOnClickListener {
            val dialog: DialogFragment = AcOptsDialog(currStateNum)
            dialog.setCancelable(false)
            dialog.show(supportFragmentManager, "Single Choice Dialog")
        }
    }

    /**
     * overridden interface function that updates status value
     */
    override fun onPosBtnClick(pos: Int) {
        currStateNum = pos
        status!!.setText(OPTS[pos])

        sendState()
    }

    /**
     * fetches state from arduino
     */
    fun fetchState() {
        status = findViewById(R.id.curr_state)
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://phrogers.com/ac/api/data/state.php"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response: JSONObject ->
                if (response.has("state")) {
                    val state = response["state"].toString()
                    status!!.setText(state)
                }
                else {
                    status!!.setText(R.string.curr_state)
                }
            },
            Response.ErrorListener { error ->
                status!!.setText("Error")

            }
        )
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)

    }

    /**
     * sends state to arduino and awaits confirmation of success
     */
    fun sendState() {

    }
}