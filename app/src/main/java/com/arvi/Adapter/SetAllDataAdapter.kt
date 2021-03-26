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
import com.arvi.Activity.NewApp.AllDataAttendanceActivity
import com.arvi.R
import de.hdodenhof.circleimageview.CircleImageView

class SetAllDataAdapter(
    var context: Context
) : RecyclerView.Adapter<SetAllDataAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvDayADR = itemView.findViewById(R.id.tvDayADR) as TextView
        var tvDateADR = itemView.findViewById(R.id.tvDateADR) as TextView
        var tvAbsentADR = itemView.findViewById(R.id.tvAbsentADR) as TextView
        var tvMissedADR = itemView.findViewById(R.id.tvMissedADR) as TextView
        var tvPresentADR = itemView.findViewById(R.id.tvPresentADR) as TextView
        var rlAbsentADR = itemView.findViewById(R.id.rlAbsentADR) as RelativeLayout
        var rlMissedADR = itemView.findViewById(R.id.rlMissedADR) as RelativeLayout
        var rlPresentADR = itemView.findViewById(R.id.rlPresentADR) as RelativeLayout
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
            holder.rlPresentADR.setOnClickListener {
                var intent = Intent(context!!,AllDataAttendanceActivity::class.java)
                context.startActivity(intent)
            }
            holder.rlAbsentADR.setOnClickListener {
                var intent = Intent(context!!,AllDataAttendanceActivity::class.java)
                context.startActivity(intent)
            }
            holder.rlMissedADR.setOnClickListener {
                var intent = Intent(context!!,AllDataAttendanceActivity::class.java)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}