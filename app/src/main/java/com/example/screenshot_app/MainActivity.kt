package com.example.screenshot_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.screenshot_app.controller.services.utils.hideOverlay
import com.example.screenshot_app.controller.services.utils.isAccessibilityServiceEnabled
import com.example.screenshot_app.controller.services.utils.requestAccessibilityPermission
import com.example.screenshot_app.controller.services.utils.requestOverlayPermission
import com.example.screenshot_app.controller.services.utils.showOverlay
import com.example.screenshot_app.view.activity.ListProjectActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class MainActivity : AppCompatActivity() {
    private lateinit var tvProjectId: TextView
    private lateinit var tvNote: TextView
    private lateinit var tvProjectName: TextView

    private lateinit var lnRequestOverlayPermission: LinearLayout
    private lateinit var lnRequestAccessibilityPermission: LinearLayout
    private lateinit var lnNextToListProjectActivity: LinearLayout
    private lateinit var lnCancelTargetProjectId: LinearLayout

    private lateinit var btnSwitchOverlayPermission: SwitchMaterial
    private lateinit var btnSwitchAccessibilityPermission: SwitchMaterial
    private lateinit var btnSwitchOverlayOption: SwitchMaterial

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvProjectId = findViewById(R.id.tvProjectId)
        tvNote = findViewById(R.id.tvNote)
        tvProjectName = findViewById(R.id.tvProjectName)
        lnRequestOverlayPermission = findViewById(R.id.lnRequestOverlayPermission)
        lnRequestAccessibilityPermission = findViewById(R.id.lnRequestAccessibilityPermission)
        lnNextToListProjectActivity = findViewById(R.id.lnNextToListProjectActivity)
        lnCancelTargetProjectId = findViewById(R.id.lnCancelTargetProjectId)

        btnSwitchOverlayPermission = findViewById(R.id.btnSwitchOverlayPermission)
        btnSwitchAccessibilityPermission = findViewById(R.id.btnSwitchAccessibilityPermission)
        btnSwitchOverlayOption = findViewById(R.id.btnSwitchOverlayOption)

        val projectId = intent.getStringExtra("PROJECT_ID") ?: ""
        val projectTitle = intent.getStringExtra("PROJECT_TITLE") ?: "Chưa chọn"
        tvProjectName.text = projectTitle
        saveProjectId(projectId)
        tvProjectId.text = projectId
        if (projectId.isNotEmpty() && projectTitle.isNotEmpty()) {
            lnCancelTargetProjectId.visibility = View.VISIBLE
        }
        btnSwitchOverlayPermission.setOnClickListener { requestOverlayPermission() }
        btnSwitchAccessibilityPermission.setOnClickListener { requestAccessibilityPermission() }
        lnNextToListProjectActivity.setOnClickListener {
            val intent = Intent(this, ListProjectActivity::class.java)
            startActivity(intent)
        }
        btnSwitchOverlayOption.setOnClickListener{optionShowOrHide()}
        lnCancelTargetProjectId.setOnClickListener {
            tvProjectName.text = "Chưa chọn"
            tvProjectId.text = ""
            removeProjectId()
            lnCancelTargetProjectId.visibility = View.GONE
        }
        checkPermissions()
    }

    @SuppressLint("SetTextI18n")
    private fun checkPermissions() {
        val isOverlayEnabled = Settings.canDrawOverlays(this)
        val isAccessibilityEnabled = isAccessibilityServiceEnabled()
        btnSwitchOverlayPermission.isChecked = isOverlayEnabled
        btnSwitchAccessibilityPermission.isChecked = isAccessibilityEnabled
        tvNote.visibility = if (isAccessibilityEnabled) View.GONE else View.VISIBLE
        if (!isOverlayEnabled) {
            btnSwitchOverlayOption.isEnabled = false
        } else {
            btnSwitchOverlayOption.isEnabled = true
        }

    }

    private fun optionShowOrHide(){
        if(btnSwitchOverlayOption.isChecked)
            showOverlay()
        else
            hideOverlay()
    }
    private fun saveProjectId(projectId: String) {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("PROJECT_ID", projectId)
            apply()
        }
    }

    private fun removeProjectId() {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("PROJECT_ID")
            apply()
        }
    }


    override fun onResume() {
        super.onResume()
        checkPermissions()
    }
}