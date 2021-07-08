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
                    fullScreen()
                    dialog?.cancel()
                }
                .setNegativeButton(
                    getString(R.string.txt_dialog_negative_button)
                ) { _, _ ->
                    fullScreen()
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }



    private fun fullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
            requireActivity().window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }
}

interface VertexNameEntered {
    fun receiveName(name: String)
}