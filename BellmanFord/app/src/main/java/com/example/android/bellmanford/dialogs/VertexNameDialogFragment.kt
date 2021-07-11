package com.example.android.bellmanford.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.example.android.bellmanford.R
import com.example.android.bellmanford.util.AppFullscreen

class VertexNameDialogFragment(val nameEntered: VertexNameEntered) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {


            val builder = AlertDialog.Builder(it)

            val newVertexNameDialogView =
                layoutInflater.inflate(R.layout.dialog_vertex_name_picker, null)

            val nameEditText =
                newVertexNameDialogView.findViewById<EditText>(R.id.dialog_vertex_name_picker_et_name)

            builder.setView(newVertexNameDialogView)
                .setTitle(getString(R.string.title_dialog_vertex_name_picker))
                .setPositiveButton(
                    getString(R.string.txt_dialog_positive_button)
                ) { _, _ ->
                    nameEntered.receiveName(nameEditText.text.toString())
                    dialog?.cancel()
                }
                .setNegativeButton(
                    getString(R.string.txt_dialog_negative_button)
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDetach() {
        super.onDetach()
        AppFullscreen.turnFullscreen(requireActivity())
    }
}

interface VertexNameEntered {
    fun receiveName(name: String)
}