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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class GameActivity  : AppCompatActivity() {

    var gameId: Int = 0
    var tempImageUri: String = ""
    lateinit var dbHandler: MySQLDatabaseConnector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameId = intent.getIntExtra("id", 0)
        val type = intent.getStringExtra("name") ?: "boardgame"

        val gameTitleView = findViewById<TextView>(R.id.gameTitle)
        gameTitleView.setText(R.string.boardgameTitle)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbHandler = MySQLDatabaseConnector(this@GameActivity)
                val game = dbHandler.getGame(gameId)


                withContext(Dispatchers.Main) {
                    var imageLink: String
                    if (game.imageLink == "BRAK"){
                        imageLink = game.thumbnailLink
                    }else{
                        imageLink = game.imageLink
                    }

                    val imageView = findViewById<ImageView>(R.id.mainPhoto)
                    Glide.with(this@GameActivity).load(imageLink).placeholder(R.drawable.spinner).centerCrop().into(imageView)

                    val titleValue = findViewById<TextView>(R.id.titleValue)
                    val yearValue = findViewById<TextView>(R.id.yearValue)
                    val rankingValue = findViewById<TextView>(R.id.rankingValue)

                    titleValue.setText(game.title)
                    yearValue.setText(game.year)
                    rankingValue.setText(if (game.ranking == 0) "BRAK" else game.ranking.toString())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GameActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
                    this@GameActivity.finish()
                }
            }
        }

    }

    fun goBack(view: View){
        this.finish()
    }
}