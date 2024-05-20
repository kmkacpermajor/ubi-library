package edu.put.inf151753

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var dbHandler: MySQLDatabaseConnector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbHandler = MySQLDatabaseConnector(this@MainActivity)
                updateInfo(true)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
                    this@MainActivity.finish()
                }
            }
        }
    }

    override fun onResume(){
        super.onResume()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbHandler = MySQLDatabaseConnector(this@MainActivity)
                updateInfo(false)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
                    this@MainActivity.finish()
                }
            }
        }
    }

    fun updateInfo(goToConf: Boolean){
        if (!dbHandler.firstTime()){
            var count = dbHandler.getCount().toString()
            if (count == "0") count = "BRAK"

            findViewById<TextView>(R.id.userValue).setText(dbHandler.getUser())
            findViewById<TextView>(R.id.gamesValue).setText(count)
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

    fun goToSync(view: View){
        val intent = Intent(this, SyncActivity::class.java)
        startActivity(intent)
    }

    fun goToConf(){
        val intent = Intent(this, ConfActivity::class.java)
        startActivity(intent)
    }

    fun clearDB(view: View){
        CoroutineScope(Dispatchers.IO).launch {
            dbHandler.clearGames()
            dbHandler.clearTech()
            dbHandler.clearPhotos()

            this@MainActivity.finish()
        }
    }

    fun goBack(view: View){
        this.finish()
    }
}