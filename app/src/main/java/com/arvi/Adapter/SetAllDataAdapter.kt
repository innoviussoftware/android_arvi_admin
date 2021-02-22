package com.arvi.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.R
import de.hdodenhof.circleimageview.CircleImageView

class SetAllDataAdapter(
    var context: Context
) : RecyclerView.Adapter<SetAllDataAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgVwProfileRAD = itemView.findViewById(R.id.imgVwProfileRAD) as CircleImageView
        var tvNameRAD = itemView.findViewById(R.id.tvNameRAD) as TextView
        var imgVwStatusRAD = itemView.findViewById(R.id.imgVwStatusRAD) as ImageView
        var tvDateRAD = itemView.findViewById(R.id.tvDateRAD) as TextView
        var tvStartTimeRAD = itemView.findViewById(R.id.tvStartTimeRAD) as TextView
        var tvEndTimeRAD = itemView.findViewById(R.id.tvEndTimeRAD) as TextView
        var tvTotalHrRAD = itemView.findViewById(R.id.tvTotalHrRAD) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_all_data, parent, false)
        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if (position  == 0) {
                holder.imgVwStatusRAD.background = context.resources.getDrawable(R.drawable.overtime_status)
            } else if (position / 2 == 0) {
                holder.imgVwStatusRAD.background = context.resources.getDrawable(R.drawable.late_status)
            } else {
                holder.imgVwStatusRAD.background = context.resources.getDrawable(R.drawable.wfh_status)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}