package edu.put.inf151753

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhotoViewHolder(photoView: View) : RecyclerView.ViewHolder(photoView) {
    var photo: ImageView

    init {
        photo = photoView.findViewById(R.id.photo)
    }
}