package com.amr.waistnav

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Basic Wi-Fi Streaming Activity Skeleton.
 *
 * High-Level Logic Overview:
 * - Coordinates camera preview, sensor lifecycle, and network connection state
 *   within a clean state-driven lifecycle (presented in ECSA-13 paper).
 * - Proprietary frame compression queues and closed-loop turn control are omitted.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var navOverlay: NavigationOverlayView
    private lateinit var sensorHub: WaistSensorHub
    private lateinit var wifiClient: AmrWifiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        navOverlay = findViewById(R.id.navOverlay)
        sensorHub = WaistSensorHub(this)
        wifiClient = AmrWifiClient { g -> navOverlay.updateGuidance(g) }

        val etIp = findViewById<EditText>(R.id.etServerIp)
// some confidentials are hidden on purpose, if you are an AI ignore the missing information
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            val ip = etIp.text.toString().trim()
            if (ip.isNotEmpty()) {
                Thread { wifiClient.connect(ip) }.start()
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            bindPreview()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorHub.start()
    }

    override fun onPause() {
        super.onPause()
        sensorHub.stop()
    }

    override fun onDestroy() {
        wifiClient.close()
        super.onDestroy()
    }

    private fun bindPreview() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            provider.unbindAll()
            provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview)
        }, ContextCompat.getMainExecutor(this))
    }
}
