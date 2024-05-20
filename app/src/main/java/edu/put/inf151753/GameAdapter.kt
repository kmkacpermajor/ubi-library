package edu.put.inf151753

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GameAdapter(var activity: GameListActivity, var context: Context, var games: List<Game>) :
    RecyclerView.Adapter<GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        return GameViewHolder(LayoutInflater.from(context).inflate(R.layout.game_view, parent, false))
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.gameId.setText(games[position].gameId.toString())
        Glide.with(activity).load(games[position].thumbnailLink).placeholder(R.drawable.spinner).centerCrop().into(holder.thumbnail)
        holder.title.setText(games[position].title)
        holder.year.setText("(${games[position].year})")
        holder.button.setOnClickListener{ view -> activity.showGameClickedHandler(games[position].gameId) }
    }

    override fun getItemCount(): Int {
        return games.size
    }
}