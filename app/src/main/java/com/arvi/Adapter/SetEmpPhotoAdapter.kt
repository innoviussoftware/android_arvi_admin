package com.arvi.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.R
import com.arvi.Utils.AppConstants
import com.squareup.picasso.Picasso

class SetEmpPhotoAdapter(

    var context: Context,
    var myList: List<String>
) : RecyclerView.Adapter<SetEmpPhotoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgVwPhoto = itemView.findViewById(R.id.imgVwPhoto) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_photo, parent, false)
        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            var path = AppConstants.IMAGE_URL + myList.get(position)
            Picasso.with(context)
                .load(path)
                .fit()
                .into(holder.imgVwPhoto)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}