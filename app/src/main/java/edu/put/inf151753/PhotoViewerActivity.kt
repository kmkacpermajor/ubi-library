package edu.put.inf151753

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.io.File

class PhotoViewerActivity : AppCompatActivity() {
    lateinit var path: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_viewer)

        path = intent.getStringExtra("path") ?: ""
        val imageView = findViewById<ImageView>(R.id.imageView)

        Picasso.get().load(File(path)).into(imageView)
    }

    fun goBack(view: View){
        this.finish()
    }

    fun deletePhoto(view: View){
        val dbHandler = DatabaseConnector(this, null, null, 1)
        dbHandler.deletePhoto(path)
        this.finish()
    }
}