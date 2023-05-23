package edu.put.inf151753

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class ConfActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conf)
    }

    fun createAccount(view: View){
        val dbHandler = DatabaseConnector(this, null, null, 1)

        val username = findViewById<EditText>(R.id.usernameValue).text.toString()

        dbHandler.insertUser(username)
        Toast.makeText(this, "Zalogowano", Toast.LENGTH_SHORT).show()

        this.finish()
        goToSync()
    }

    fun goToSync(){
        val intent = Intent(this, SyncActivity::class.java)
        startActivity(intent)
    }
}