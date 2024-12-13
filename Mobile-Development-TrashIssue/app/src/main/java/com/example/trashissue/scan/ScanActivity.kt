package com.example.trashissue.scan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.trashissue.R
import com.example.trashissue.home.HomeActivity
import com.example.trashissue.api.ApiClient
import com.example.trashissue.home.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class ScanActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ScanActivity"
        private const val PICK_IMAGE_REQUEST = 1001
        private const val CAMERA_REQUEST_CODE = 1002
    }

    private lateinit var btnCamera: Button
    private lateinit var btnGallery: Button
    private lateinit var btnAnalyze: Button
    private lateinit var ivHome: ImageView
    private lateinit var ivAdd: ImageView
    private lateinit var ivSettings: ImageView
    private lateinit var ivPlaceholder: ImageView
    private lateinit var tvResult: TextView
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_activity)

        initializeUI()
    }

    private fun initializeUI() {
        btnCamera = findViewById(R.id.btn_1)
        btnGallery = findViewById(R.id.btn_2)
        btnAnalyze = findViewById(R.id.btn_3)
        ivHome = findViewById(R.id.iv_home)
        ivAdd = findViewById(R.id.iv_add)
        ivSettings = findViewById(R.id.iv_settings)
        ivPlaceholder = findViewById(R.id.placeholder_image)
        tvResult = findViewById(R.id.tv_result)

        btnCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }

        btnGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnAnalyze.setOnClickListener {
            photoUri?.let {
                tvResult.text = "Menganalisis gambar..."
                analyzeImageWithApi(it)
            } ?: Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
        }

        ivHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        ivAdd.setOnClickListener {
            Toast.makeText(this, "Anda Sudah Disini Kawan", Toast.LENGTH_SHORT).show()
        }

        ivSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    private fun analyzeImageWithApi(uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = getFileFromUri(uri)
                Log.d(TAG, "File size: ${file.length()} bytes")
                Log.d(TAG, "File path: ${file.absolutePath}")

                val requestFile = file.asRequestBody("image/*".toMediaType())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = ApiClient.instance.predictTrash(imagePart)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.let { predictResponse ->
                            Log.d(TAG, "Success Response: $predictResponse")

                            if (predictResponse.status == "success") {
                                val resultText = """
                                    Hasil Prediksi:
                                    ${predictResponse.data?.result}
                                    
                                    Saran:
                                    ${predictResponse.data?.suggestion}
                                """.trimIndent()
                                tvResult.text = resultText
                            } else {
                                tvResult.text = "Error: ${predictResponse.message}"
                            }
                        } ?: run {
                            tvResult.text = "Error: Response body is empty"
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "Error Response: $errorBody")
                        Log.e(TAG, "Response Code: ${response.code()}")

                        tvResult.text = "Error: Server returned ${response.code()}. $errorBody"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during API call", e)
                withContext(Dispatchers.Main) {
                    tvResult.text = "Gagal menganalisis gambar: ${e.message}"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            photoUri = data?.data
            photoUri?.let {
                Glide.with(this).load(it).into(ivPlaceholder)
            } ?: run {
                Toast.makeText(this, "Gambar tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imagePath = data?.getStringExtra("image_path")
            if (imagePath != null) {
                photoUri = Uri.fromFile(File(imagePath))
                Glide.with(this).load(photoUri).into(ivPlaceholder)
            } else {
                Toast.makeText(this, "Gambar tidak ditemukan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}