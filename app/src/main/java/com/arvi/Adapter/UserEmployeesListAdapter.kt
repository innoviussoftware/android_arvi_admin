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

class UserEmployeesListAdapter(
    var context: Context
) : RecyclerView.Adapter<UserEmployeesListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivProfileRUED = itemView.findViewById(R.id.ivProfileRUED) as ImageView
        var tvEmployeeNameRUED = itemView.findViewById(R.id.tvEmployeeNameRUED) as TextView
        var tvActiveStatusRUED = itemView.findViewById(R.id.tvActiveStatusRUED) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_user_employee_data, parent, false)
        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if(position / 2 == 0) {
                holder.tvActiveStatusRUED.setText(context.resources.getString(R.string.active_ttl))
                holder.tvActiveStatusRUED.setTextColor(context.resources.getColor(R.color.active_status))
            } else/* if (position / 2 == 0)*/ {
                holder.tvActiveStatusRUED.setText(context.resources.getString(R.string.inactive_ttl))
                holder.tvActiveStatusRUED.setTextColor(context.resources.getColor(R.color.inactive_status))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}