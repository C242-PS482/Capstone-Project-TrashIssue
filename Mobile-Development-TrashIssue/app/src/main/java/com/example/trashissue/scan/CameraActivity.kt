package com.example.trashissue.scan

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.camera.view.PreviewView
import com.example.trashissue.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnCapture: Button
    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService

    private var imageCapture: ImageCapture? = null

    private val requestCameraPermission = ActivityResultContracts.RequestPermission()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Inisialisasi komponen UI
        btnBack = findViewById(R.id.btn_back)
        btnCapture = findViewById(R.id.btn_capture)
        previewView = findViewById(R.id.preview_view)

        // Inisialisasi executor untuk kamera
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Tombol kembali
        btnBack.setOnClickListener { onBackPressed() }

        // Cek dan minta izin kamera
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 100)
        }

        // Tombol ambil foto
        btnCapture.setOnClickListener { capturePhoto() }
    }

    // Menangani hasil permintaan izin
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview kamera
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            // ImageCapture untuk mengambil foto
            imageCapture = ImageCapture.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Kamera belakang
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind semua untuk memastikan konfigurasi ulang
                cameraProvider.unbindAll()

                // Bind preview dan capture ke lifecycle
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Gagal memulai kamera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun capturePhoto() {
        val imageCapture = imageCapture ?: return

        // Membuat file untuk menyimpan foto
        val photoFile = createFile()

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Mengambil foto
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Foto berhasil disimpan: ${photoFile.absolutePath}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Kirim hasil path gambar ke ScanActivity
                    val resultIntent = Intent().apply {
                        putExtra("image_path", photoFile.absolutePath)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Gagal mengambil foto: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun createFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
        val storageDir = getExternalFilesDir(null)
        return File(storageDir, "IMG_$timestamp.jpg")
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
