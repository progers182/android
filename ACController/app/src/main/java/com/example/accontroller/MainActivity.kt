package com.example.accontroller

import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
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

    override fun onPosBtnClick(opts: Array<String?>?, pos: Int) {
        currStateNum = pos
        status!!.setText(opts!![pos])
    }

    fun fetchState() {
        currStateNum = 5
        Handler().postDelayed({
            status!!.setText(OPTS[5])
        }, 3000)
    }
}