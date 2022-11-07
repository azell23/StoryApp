package com.example.mystoryapp

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mystoryapp.databinding.ActivityUploadBinding
import com.example.mystoryapp.datastore.AuthPreference
import com.example.mystoryapp.util.*
import com.example.mystoryapp.util.Constanta.Companion.CAMERA_X_RESULT
import com.example.mystoryapp.util.Constanta.Companion.REQ_CODE
import com.example.mystoryapp.util.Constanta.Companion.REQ_PERMISSION
import com.example.mystoryapp.viewmodel.Auth
import com.example.mystoryapp.viewmodel.StoryModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var viewModel: StoryModel
    private lateinit var authModel: Auth
    private lateinit var authPreference: AuthPreference
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_CODE){
            if (!allPermission()) {
                Toast.makeText(this, "Tidak mendapat perizinan", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermission() = REQ_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermission()){
            ActivityCompat.requestPermissions(this, REQ_PERMISSION, REQ_CODE)
        }
        authPreference = AuthPreference(this)
        viewModel = ViewModelProvider(this)[StoryModel::class.java]
        authModel = ViewModelProvider(this, PreferenceFactory(authPreference))[Auth::class.java]

        binding.btnCamera.setOnClickListener { cameraX() }
        binding.btnUpload.setOnClickListener { upload()  }
        binding.btnGallery.setOnClickListener { gallery() }

    }

    private fun cameraX(){
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.ivDetail.setImageBitmap(result)
        }
    }


    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadActivity)
            getFile = myFile
            binding.ivDetail.setImageURI(selectedImg)
        }
    }
    private fun gallery(){
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Foto")
        launcherIntentGallery.launch(chooser)
    }
    private fun upload() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val reqImage = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                reqImage
            )
            val storyDesc = binding.etDesc.text.toString().toRequestBody("text/plain".toMediaType())

            if (binding.etDesc.text.isEmpty()){
                binding.etDesc.error = "Tambahkan deskrispi"
            } else {
                authModel.getToken().observe(this){ token ->
                    viewModel.uploadStory("Bearer $token", imageMultiPart, storyDesc)
                    viewModel._storyResponse.observe(this){ response ->
                        if ( !response.error){
                            Toast.makeText(this, "Berhasil Upload Story ", Toast.LENGTH_SHORT).show()
                            val builder = AlertDialog.Builder(this)
                            builder.setMessage("Upload Selesai")
                            builder.setPositiveButton("No"){dialog, _ ->
                                dialog.cancel()
                            }
                            builder.setPositiveButton("Yes"){_, _ ->
                                this.getSharedPreferences("data",0).edit().clear()
                                    .apply()
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra(Constanta.SUCSSESS_UPLOAD, true)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                                finish()
                            }
                            val alert = builder.create()
                            alert.show()
                        }
                    }
                }
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}