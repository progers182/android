package com.example.accontroller

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
        currStateNum = 5
        Handler().postDelayed({
            status!!.setText(OPTS[5])
        }, 3000)

        val textView = findViewById<TextView>(R.id.textView);
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://www.phrogers.com/ac/api/state.php"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                textView.text = "Response is: ${response}"
            },
            Response.ErrorListener { textView.text = "That didn't work!" })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

    }

    /**
     * sends state to arduino and awaits confirmation of success
     */
    fun sendState() {

    }
}