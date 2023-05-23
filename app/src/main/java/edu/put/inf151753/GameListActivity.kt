package edu.put.inf151753

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class GameListActivity  : AppCompatActivity() {
    lateinit var games: MutableList<Game>
    lateinit var adapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_list)

        val type = intent.getStringExtra("type") ?: "boardgame"

        val listTitleView: TextView = findViewById(R.id.listTitle)
        if (type == "boardgame")
            listTitleView.setText(R.string.boardgameListTitle)
        else
            listTitleView.setText(R.string.boardgameexpansionListTitle)

        val dbHandler = DatabaseConnector(this, null, null, 1)
        val recyclerView: RecyclerView = findViewById(R.id.gamesListView)



        games = dbHandler.getGames(type)
        games.sortWith(Comparator { lhs, rhs ->
            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
            if (lhs.title > rhs.title) 1 else if (lhs.title < rhs.title) -1 else 0
        })

        adapter = GameAdapter(this, applicationContext, games)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    fun showGameClickedHandler(gameId: Int, type: String){
        var intent = Intent(this, GameActivity::class.java)
        intent.putExtra("id", gameId)
        intent.putExtra("type", type)
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