package com.example.accontroller

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

val NUM_OPTS = 5
class AcOptsDialog(var pos: Int = NUM_OPTS) : DialogFragment() {

    interface SingleChoiceListener {
        fun onPosBtnClick(
            opts: Array<String?>?,
            pos: Int
        )

    }

    var mListener: SingleChoiceListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = try {
            context as SingleChoiceListener?
        } catch (e: Exception) {
            throw ClassCastException(
                activity.toString().toString() + " SingleChoiceListener must be implemented"
            )
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val opts: Array<String?> =
            activity!!.resources.getStringArray(R.array.ac_opts)
        builder.setTitle(R.string.change_state)
            .setSingleChoiceItems(opts, pos,
                DialogInterface.OnClickListener { _, i -> pos = i })
            .setPositiveButton("Ok", DialogInterface.OnClickListener { _, _ ->
                    mListener!!.onPosBtnClick(opts, pos)
                })
            .setNegativeButton("Cancel") {
                    interf, _ -> interf.dismiss()
            }
        return builder.create()
    }
}