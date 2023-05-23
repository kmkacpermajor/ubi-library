package edu.put.inf151753

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

class PhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val gameId = intent.getIntExtra("id", 0)

        val tempImageUri = initTempUri()

        var mGetContent = registerForActivityResult(ActivityResultContracts.GetContent()){
                result ->
            if (result != null){
                val path = saveCapturedImage(tempImageUri)
                val dbHandler = DatabaseConnector(this, null, null, 1)
                dbHandler.insertPhoto(gameId, path)
                this.finish()
            }
        }

        findViewById<Button>(R.id.addFromGalleryButton).setOnClickListener{
            mGetContent.launch("image/*")

        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){
            val path = saveCapturedImage(tempImageUri)
            val dbHandler = DatabaseConnector(this, null, null, 1)
            dbHandler.insertPhoto(gameId, path)
            this.finish()
        }

        findViewById<Button>(R.id.takePhotoButton).setOnClickListener{
            resultLauncher.launch(tempImageUri)
        }
    }

    fun saveCapturedImage(uri: Uri): String {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

            else -> {
                val source = ImageDecoder.createSource(this.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        }
        val file = File(applicationContext.applicationInfo.dataDir,"image_"+System.currentTimeMillis().toString()+".jpg")

        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)

        out.close()

        return file.path
    }

    fun initTempUri(): Uri {
        val tempImagesDir = File(applicationContext.filesDir, getString(R.string.imagesDir))

        tempImagesDir.mkdir()

        val tempImage = File(tempImagesDir, "photo")

        return FileProvider.getUriForFile(applicationContext, getString(R.string.authorities), tempImage)
    }

    fun goBack(view: View){
        this.finish()
    }
}