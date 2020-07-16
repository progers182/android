package com.example.accontroller

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import org.json.JSONObject


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
        // display if current state is not the same as the last command
        isPending()

        // TODO add weather command that tells the arduino to make decisions based on current weather data (every 30 minutes or so)

        status = findViewById(R.id.curr_state)

        // create onClickListener for change state button
        val changeState = findViewById<Button>(R.id.change_state)
        changeState.setOnClickListener {
            val dialog: DialogFragment = AcOptsDialog(currStateNum)
            dialog.setCancelable(false)
            dialog.show(supportFragmentManager, "Single Choice Dialog")
        }

        // refreshes activity
        val refresh_btn = findViewById<ImageButton>(R.id.refresh)
        refresh_btn.setOnClickListener{
            finish();
            startActivity(getIntent());
        }
    }

    /**
     * overridden interface function that updates status value
     */
    override fun onPosBtnClick(pos: Int) {
        sendState(pos)
        // determine whether or not to display pendingState text
        isPending()
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

                if (response.has("username")) {
                    var username = findViewById<TextView>(R.id.curr_user)
                    username.setText(response["username"].toString())
                }
                // update actual current state
                currStateNum = OPTS.indexOf(state)
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
    fun sendState(command: Int) { // display pending update until arduino board reports that its state has updated
        val request = Requests(this)

        // POST request body
        val params = HashMap<String, Int>()
        params["command"] = command
        val body = JSONObject(params.toMap())

        request.makeRequest("POST", "create", Pair("table", "2"), body, { response ->
            if (response.has("message")) {
                val successMsg =
                    Toast.makeText(this, response["message"].toString(), Toast.LENGTH_SHORT)
//                successMsg.show()
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

    fun isPending() {
        val request = Requests(this)
        val pendingMsg = findViewById<LinearLayout>(R.id.pending_update)
        var pendingState = findViewById<TextView>(R.id.pending_state)

        request.makeRequest("GET", "queue", responseHandler =
        {response ->
            if ((response.has("command") && response.has("curr_state") && response["command"] == response["curr_state"])
                || (response.has("isUpdated") && response["isUpdated"] as Boolean)) {

                pendingMsg.visibility = View.INVISIBLE
            }
            else {
                if (response.has("command")) {
                    var pos: String = response["command"].toString()
                    pendingState.setText(OPTS[pos.toInt()])
                }
                pendingMsg.visibility = View.VISIBLE
            }
        },
        errorResponse = {error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
        })
    }
}