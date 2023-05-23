package edu.put.inf151753

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameViewHolder(gameView: View) : RecyclerView.ViewHolder(gameView) {
    var gameId: TextView
    var thumbnail: ImageView
    var title: TextView
    var year: TextView
    var button: Button

    init {
        gameId = gameView.findViewById(R.id.gameId)
        thumbnail = gameView.findViewById(R.id.thumbnail)
        title = gameView.findViewById(R.id.titleValue)
        year = gameView.findViewById(R.id.yearValue)
        button = gameView.findViewById(R.id.showGame)
    }
}