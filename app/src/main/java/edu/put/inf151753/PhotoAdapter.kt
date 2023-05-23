package edu.put.inf151753

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class PhotoAdapter(var activity: GameActivity, var context: Context, var photos: List<Photo>) :
    RecyclerView.Adapter<PhotoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(LayoutInflater.from(context).inflate(R.layout.photo_view, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Glide.with(activity).load(File(photos[position].path)).placeholder(R.drawable.spinner).centerCrop().into(holder.photo)
        holder.photo.setOnClickListener{
            activity.showPhoto(photos[position])
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    fun updatePhotos(newPhotos: List<Photo>){
        photos = newPhotos
        notifyDataSetChanged()
    }
}