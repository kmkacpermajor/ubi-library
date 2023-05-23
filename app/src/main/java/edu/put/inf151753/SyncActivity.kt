package edu.put.inf151753

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SyncActivity  : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        updateDate()
    }

    fun updateDate(){
        val dbHandler = DatabaseConnector(this, null, null, 1)

        val syncValue = findViewById<TextView>(R.id.syncValue)
        syncValue.setText(toDate(dbHandler.getTimestamp()))
    }

    fun sychronize(view: View){
        val dbHandler = DatabaseConnector(this, null, null, 1)
        val current_timestamp = System.currentTimeMillis() / 1000
        val timestamp = dbHandler.getTimestamp()

        if (current_timestamp - timestamp < 86400){
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.syncSure)
                .setCancelable(false)
                .setPositiveButton("Tak") { dialog, id ->
                    dbHandler.synchronize()
                }
                .setNegativeButton("Nie") { dialog, id ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }else{
            dbHandler.synchronize()
        }


    }

    fun goBack(view: View){
        this.finish()
    }
}