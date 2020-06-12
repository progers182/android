package com.example.accontroller

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.w3c.dom.Text


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
        sendState(pos)
    }

    /**
     * fetches state from arduino
     */
    fun fetchState() {
        status = findViewById(R.id.curr_state)
        // Instantiate the RequestQueue.
        val request = Requests(this)
        request.makeRequest("GET", "state", responseHandler = { response ->
            if (response.has("state")) {
                val state = response["state"].toString()
                status!!.setText(state)
            } else {
                status!!.setText(R.string.curr_state)
            }
        }, errorResponse = {
            status!!.setText("Error")
        })
    }

    /**
     * sends state to arduino and awaits confirmation of success
     */
    fun sendState(command: Int) {
        val request = Requests(this)

        // POST request body
        val params = HashMap<String, Int>()
        params["command"] = command
        val body = JSONObject(params.toMap())

        request.makeRequest("POST", "create", Pair("table", "2"), body, { response ->
            if (response.has("message")) {
                val successMsg =
                    Toast.makeText(this, response["message"].toString(), Toast.LENGTH_SHORT)
                successMsg.show()
            } else {
                val errorMsg =
                    Toast.makeText(this, "Error reading response from API", Toast.LENGTH_SHORT)
                errorMsg.show()
            }
        }, { error ->
            val errorMsg = Toast.makeText(this, "Error: request not received", Toast.LENGTH_SHORT)
            errorMsg.show()
        })
    }

}