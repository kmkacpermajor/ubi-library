package edu.put.inf151753

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateInfo(true)

    }

    override fun onResume(){
        super.onResume()

        updateInfo(false)
    }

    fun updateInfo(goToConf: Boolean){
        val dbHandler = DatabaseConnector(this, null, null, 1)
        if (!dbHandler.firstTime()){
            var count = dbHandler.getCount("boardgame").toString()
            if (count == "0") count = "BRAK"

            var count_exp = dbHandler.getCount("boardgameexpansion").toString()
            if (count_exp == "0") count_exp = "BRAK"

            findViewById<TextView>(R.id.userValue).setText(dbHandler.getUser())
            findViewById<TextView>(R.id.gamesValue).setText(count)
            findViewById<TextView>(R.id.addonValue).setText(count_exp)
            findViewById<TextView>(R.id.syncValue).setText(toDate(dbHandler.getTimestamp()))
        }else{
            if (goToConf)
                goToConf()
        }
    }

    fun goToGamesList(view: View){
        val intent = Intent(this, GameListActivity::class.java)
        intent.putExtra("type", "boardgame")
        startActivity(intent)
    }

    fun goToAddonsList(view: View){
        val intent = Intent(this, GameListActivity::class.java)
        intent.putExtra("type", "boardgameexpansion")
        startActivity(intent)
    }

    fun goToSync(view: View){
        val intent = Intent(this, SyncActivity::class.java)
        startActivity(intent)
    }

    fun goToConf(){
        val intent = Intent(this, ConfActivity::class.java)
        startActivity(intent)
    }

    fun clearDB(view: View){
        val dbHandler = DatabaseConnector(this, null, null, 1)
        dbHandler.clearGames()
        dbHandler.clearTech()
        dbHandler.clearPhotos()

        this.finish()
    }

    fun goBack(view: View){
        this.finish()
    }
}