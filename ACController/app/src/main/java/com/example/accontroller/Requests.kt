package com.example.accontroller

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Requests(context: Context) {

    val body = JSONObject()
    val table = 0
    var url = "https://phrogers.com/ac/api/data/"
    var queue: RequestQueue

    val requestType: HashMap<String, Int> = hashMapOf(
        "POST" to Request.Method.POST,
        "GET" to Request.Method.GET
    )

    init {
        queue = Volley.newRequestQueue(context)
    }

    fun makeRequest(
        type: String,
        action: String,
        actionParams: Pair<String, String>? = null,
        body: JSONObject? = null,
        responseHandler: (JSONObject) -> Any,
        errorResponse: (VolleyError) -> Any
    ) {
        // build url
        this.url += action + ".php"
        // add GET params for REST API
        if (type == "POST") {
            this.url +=  "?" + actionParams!!.first + '=' + actionParams!!.second
        }

        val jsonObjectRequest = JsonObjectRequest(this.requestType[type]!!, this.url, body,
            Response.Listener { response: JSONObject -> responseHandler(response) },
            Response.ErrorListener { error -> errorResponse(error) }
        )

        this.queue.add(jsonObjectRequest)
    }
}