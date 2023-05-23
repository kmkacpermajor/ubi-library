package edu.put.inf151753

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.io.File

class GameActivity  : AppCompatActivity() {

    lateinit var adapter: PhotoAdapter
    lateinit var photos: MutableList<Photo>
    var gameId: Int = 0
    var tempImageUri: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameId = intent.getIntExtra("id", 0)
        val type = intent.getStringExtra("name") ?: "boardgame"

        val dbHandler = DatabaseConnector(this, null, null, 1)

        val gameTitleView = findViewById<TextView>(R.id.gameTitle)
        if (type == "boardgame")
            gameTitleView.setText(R.string.boardgameTitle)
        else
            gameTitleView.setText(R.string.boardgameexpansionTitle)

        val game = dbHandler.getGame(gameId)

        var imageLink: String
        if(game.imageLink == "BRAK"){
            imageLink = game.thumbnailLink
        }else{
            imageLink = game.imageLink
        }

        val imageView = findViewById<ImageView>(R.id.mainPhoto)
        Picasso.get().load(imageLink).placeholder(R.drawable.spinner).fit().centerCrop().into(imageView)

        val titleValue = findViewById<TextView>(R.id.titleValue)
        val yearValue = findViewById<TextView>(R.id.yearValue)
        val rankingValue = findViewById<TextView>(R.id.rankingValue)

        titleValue.setText(game.title)
        yearValue.setText(game.year)
        rankingValue.setText(game.ranking.toString())

        val recyclerView: RecyclerView = findViewById(R.id.photoRecyclerView)

        photos = dbHandler.getPhotos(gameId)

        adapter = PhotoAdapter(this, applicationContext, photos)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    fun showPhoto(photo: Photo){
        var intent = Intent(this, PhotoViewerActivity::class.java)
        intent.putExtra("path", photo.path)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        val dbHandler = DatabaseConnector(this, null, null, 1)
        adapter.updatePhotos(dbHandler.getPhotos(gameId))
    }

    fun goToPhotos(view: View){
        var intent = Intent(this, PhotoActivity::class.java)
        intent.putExtra("id", gameId)
        startActivity(intent)
    }

    fun goBack(view: View){
        this.finish()
    }
}