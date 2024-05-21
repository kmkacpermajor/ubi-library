package edu.put.inf151753

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SyncActivity  : AppCompatActivity() {
    lateinit var dbHandler: MySQLDatabaseConnector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        CoroutineScope(Dispatchers.IO).launch {
//            try {
            dbHandler = MySQLDatabaseConnector(this@SyncActivity)
            updateDate()

//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@MainActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
//                    this@MainActivity.finish()
//                }
//            }
        }
    }

    fun updateDate(){
        val syncValue = findViewById<TextView>(R.id.syncValue)
        syncValue.setText(toDate(dbHandler.getTimestamp()))
    }

    fun sychronize(view: View){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbHandler.synchronize()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SyncActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
                    this@SyncActivity.finish()
                }
            }
        }



    }

    fun goBack(view: View){
        this.finish()
    }
}