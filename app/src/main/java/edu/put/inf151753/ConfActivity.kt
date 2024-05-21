package edu.put.inf151753

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfActivity : AppCompatActivity() {
    lateinit var dbHandler: MySQLDatabaseConnector
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conf)

        CoroutineScope(Dispatchers.IO).launch {
            try {
            dbHandler = MySQLDatabaseConnector(this@ConfActivity)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ConfActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
                    this@ConfActivity.finish()
                }
            }
        }
    }

    fun createAccount(view: View){
        val username = findViewById<EditText>(R.id.usernameValue).text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbHandler.insertUser(username)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ConfActivity, "Zalogowano", Toast.LENGTH_SHORT).show()

                    this@ConfActivity.finish()
                    goToSync()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ConfActivity, "Baza danych jest niedostępna", Toast.LENGTH_SHORT).show()
                    this@ConfActivity.finish()
                }
            }
        }

    }

    fun goToSync(){
        val intent = Intent(this, SyncActivity::class.java)
        startActivity(intent)
    }
}