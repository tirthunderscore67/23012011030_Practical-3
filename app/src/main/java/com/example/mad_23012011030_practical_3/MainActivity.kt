package com.example.mad_23012011030_practical_3

import android.Manifest
import android.content.Intent
import androidx.core.net.toUri
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Browse Button
        findViewById<Button>(R.id.buttonBrowse).setOnClickListener {
            val url = findViewById<EditText>(R.id.editTextUrl).text.toString()
            if (url.isNotBlank()) {
                Intent(Intent.ACTION_VIEW, url.toUri()).also { startActivity(it) }
            } else {
                showToast("Enter a valid URL")
            }
        }

        // Call Button
        findViewById<Button>(R.id.buttonCall).setOnClickListener {
            val phoneNumber = findViewById<EditText>(R.id.editTextPhone).text.toString()
            if (phoneNumber.isNotBlank()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                startActivity(intent)
            } else {
                showToast("Enter a phone number")
            }
        }

        // Call Log Button
        findViewById<Button>(R.id.buttonLog).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, CallLog.Calls.CONTENT_URI)
            startActivity(intent)
        }

        // Camera Button
        findViewById<Button>(R.id.buttonCamera).setOnClickListener {
            checkAndOpenCamera()
        }
    }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openCamera() else showToast("Camera permission denied")
        }

    private fun checkAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        else showToast("No camera app found")
    }

    private val pickMediaLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) showToast("Selected image: $uri")
            else showToast("No media selected")
        }

    private fun openGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showActionSelectionDialog() {
        val options = arrayOf("Open Camera", "Open Gallery", "Go to Login", "Make a Call")
        AlertDialog.Builder(this)
            .setTitle("Choose an action")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkAndOpenCamera()
                    1 -> openGallery()
                    2 -> goToLoginScreen()
                    3 -> showToast("Use Call button to dial a number")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun goToLoginScreen() {
        showToast("Login screen would open here.")
        // TODO: Start LoginActivity here
        // val intent = Intent(this, LoginActivity::class.java)
        // startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
