package com.example.screenshot_app.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.example.screenshot_app.R

class ApiConfigDialog(
    private val context: Context,
    private val onSave: (String, String) -> Unit
) {
    fun show() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_api_config, null)
        val edtBaseUrl = dialogView.findViewById<EditText>(R.id.edtBaseUrl)
        val edtToken = dialogView.findViewById<EditText>(R.id.edtToken)
        val btnSave = dialogView.findViewById<LinearLayout>(R.id.lnButtonSave)
        val btnCancel = dialogView.findViewById<LinearLayout>(R.id.blnButtonCancel)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        btnSave.setOnClickListener {
            val apiBaseUrl = edtBaseUrl.text.toString().trim()
            val token = edtToken.text.toString().trim()
            if(apiBaseUrl.isEmpty()){
                edtBaseUrl.error = "Không được để trống base url"
            }else if(token.isEmpty()){
                edtToken.error = "Không được để trống token"
            }
            else{
                onSave(apiBaseUrl, token)
                dialog.dismiss()
            }
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
