package edu.put.inf151753

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GameListActivity  : AppCompatActivity() {
    lateinit var games: MutableList<Game>
    lateinit var adapter: GameAdapter
    lateinit var dbHandler: MySQLDatabaseConnector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        val type = "boardgame"

        val listTitleView: TextView = findViewById(R.id.listTitle)
        listTitleView.setText(R.string.boardgameListTitle)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbHandler = MySQLDatabaseConnector(this@GameListActivity)
                val recyclerView: RecyclerView = findViewById(R.id.gamesListView)



                games = dbHandler.getGames()
                games.sortWith(Comparator { lhs, rhs ->
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    if (lhs.title > rhs.title) 1 else if (lhs.title < rhs.title) -1 else 0
                })
                withContext(Dispatchers.Main) {

                    adapter = GameAdapter(this@GameListActivity, applicationContext, games)

                    recyclerView.layoutManager = LinearLayoutManager(this@GameListActivity)
                    recyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GameListActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
                    this@GameListActivity.finish()
                }
            }
        }

    }

    fun showGameClickedHandler(gameId: Int){
        var intent = Intent(this, GameActivity::class.java)
        intent.putExtra("id", gameId)
        startActivity(intent)

    }

    fun sortList(view: View){
        val button = findViewById<Button>(R.id.sortButton)
        if (button.text == "ABC"){
            button.setText("123")
            games.sortWith(Comparator { lhs, rhs ->
                val left = if(lhs.year=="BRAK") 0 else lhs.year.toInt()
                val right = if(rhs.year=="BRAK") 0 else rhs.year.toInt()
                if (left > right) -1 else if (left < right) 1 else 0
            })
        }else{
            button.setText("ABC")
            games.sortWith(Comparator { lhs, rhs ->
                if (lhs.title > rhs.title) 1 else if (lhs.title < rhs.title) -1 else 0
            })
        }
        adapter.notifyDataSetChanged()
    }

    fun goBack(view: View){
        this.finish()
    }
}