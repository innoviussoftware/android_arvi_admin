package com.arvi.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AddVisitorDetailActivity
import com.arvi.R

class SetVisitorDataAdapter(
    var context: Context
) : RecyclerView.Adapter<SetVisitorDataAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlVisitorRV = itemView.findViewById(R.id.rlVisitorRV) as RelativeLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_visitors, parent, false)
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
            holder.rlVisitorRV.setOnClickListener {
                var intent = Intent(context, AddVisitorDetailActivity::class.java)
                intent.putExtra("from", "list")
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}