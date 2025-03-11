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
import com.example.screenshot_app.view.activity.BaseActivity
import com.example.screenshot_app.view.activity.ListProjectActivity
import com.example.screenshot_app.view.activity.OptionActivity
import com.google.android.material.switchmaterial.SwitchMaterial


class MainActivity : AppCompatActivity() {
    private lateinit var tvProjectId: TextView
    private lateinit var tvNote: TextView
    private lateinit var tvProjectName: TextView
    private lateinit var tvBaseUrl: TextView
    private lateinit var tvToken: TextView
    private lateinit var tvTitleBaseUrl: TextView
    private lateinit var tvTitleToken: TextView

    private lateinit var lnNextToOptionActivity: LinearLayout
    private lateinit var lnRequestOverlayPermission: LinearLayout
    private lateinit var lnRequestAccessibilityPermission: LinearLayout
    private lateinit var lnNextToListProjectActivity: LinearLayout
    private lateinit var lnCancelTargetProjectId: LinearLayout
    private lateinit var lnShowOverlay: LinearLayout
    private lateinit var lnHideOverlay: LinearLayout

    private lateinit var btnSwitchOverlayPermission: SwitchMaterial
    private lateinit var btnSwitchAccessibilityPermission: SwitchMaterial

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvProjectId = findViewById(R.id.tvProjectId)
        tvNote = findViewById(R.id.tvNote)
        tvProjectName = findViewById(R.id.tvProjectName)
        tvBaseUrl = findViewById(R.id.tvBaseUrl)
        tvToken = findViewById(R.id.tvToken)
        tvTitleBaseUrl = findViewById(R.id.tvTitleBaseUrl)
        tvTitleToken = findViewById(R.id.tvTitleToken)

        lnNextToOptionActivity = findViewById(R.id.lnNextToOptionActivity)
        lnRequestOverlayPermission = findViewById(R.id.lnRequestOverlayPermission)
        lnRequestAccessibilityPermission = findViewById(R.id.lnRequestAccessibilityPermission)
        lnNextToListProjectActivity = findViewById(R.id.lnNextToListProjectActivity)
        lnCancelTargetProjectId = findViewById(R.id.lnCancelTargetProjectId)
        lnShowOverlay = findViewById(R.id.lnShowOverlay)
        lnHideOverlay = findViewById(R.id.lnHideOverlay)

        btnSwitchOverlayPermission = findViewById(R.id.btnSwitchOverlayPermission)
        btnSwitchAccessibilityPermission = findViewById(R.id.btnSwitchAccessibilityPermission)

        loadApiConfig()
        loadProjectTitle()
        lnNextToOptionActivity.setOnClickListener {
            startActivity(Intent(this, OptionActivity::class.java))
        }
        lnShowOverlay.setOnClickListener { showOverlay() }
        lnHideOverlay.setOnClickListener { hideOverlay() }
        btnSwitchOverlayPermission.setOnClickListener { requestOverlayPermission() }
        btnSwitchAccessibilityPermission.setOnClickListener { requestAccessibilityPermission() }
        lnNextToListProjectActivity.setOnClickListener {
            val intent = Intent(this, ListProjectActivity::class.java)
            startActivity(intent)
        }
        lnCancelTargetProjectId.setOnClickListener {
            removeProjectId()
            lnCancelTargetProjectId.visibility = View.GONE
            tvProjectName.visibility = View.GONE
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
            lnShowOverlay.isEnabled = false
            lnHideOverlay.isEnabled = false
        } else {
            lnShowOverlay.isEnabled = true
            lnHideOverlay.isEnabled = true
        }

    }

    private fun saveProjectId(projectId: String) {
        val sharedPref = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("PROJECT_ID", projectId)
            apply()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadApiConfig() {
        val sharedPref = getSharedPreferences("ApiConfig", Context.MODE_PRIVATE)
        val baseUrl = sharedPref.getString("BASE_URL", "") ?: ""
        val token = sharedPref.getString("TOKEN", "") ?: ""

        tvBaseUrl.text = baseUrl
        tvToken.text = token
        if(baseUrl !== "" && token !== ""){
            tvTitleBaseUrl.visibility = View.VISIBLE
            tvTitleToken.visibility = View.VISIBLE
        }
    }

    private fun loadProjectTitle() {
        val sharedPreferences = getSharedPreferences("PROJECT", Context.MODE_PRIVATE)
        val projectId = sharedPreferences.getString("PROJECT_ID", "") ?: ""
        val projectTitle = sharedPreferences.getString("PROJECT_TITLE", "") ?: ""
        tvProjectId.text = projectId
        tvProjectName.text = projectTitle
        if (projectTitle.isEmpty() && projectId.isEmpty()) {
            tvProjectName.visibility = View.GONE
            lnCancelTargetProjectId.visibility = View.GONE
        } else {
            tvProjectName.visibility = View.VISIBLE
            lnCancelTargetProjectId.visibility = View.VISIBLE
            saveProjectId(projectId)
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