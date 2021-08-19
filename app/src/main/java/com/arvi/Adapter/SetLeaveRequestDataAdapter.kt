package com.arvi.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AddLeaveRequestActivity
import com.arvi.R
import com.crashlytics.android.Crashlytics
import java.text.ParseException
import java.text.SimpleDateFormat


class SetLeaveRequestDataAdapter(
    var context: Context,
    var alLeaveRequests: ArrayList<GetLeaveRequest_Leave>
) : RecyclerView.Adapter<SetLeaveRequestDataAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNameRLR = itemView.findViewById(R.id.tvNameRLR) as TextView
        var tvDateRLR = itemView.findViewById(R.id.tvDateRLR) as TextView
        var tvLeaveTypeRLR = itemView.findViewById(R.id.tvLeaveTypeRLR) as TextView
        var llMainRLR = itemView.findViewById(R.id.llMainRLR) as LinearLayout
       /* init {
            rlLeaveRLR.setOnClickListener(this)
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_leave_request, parent, false)
        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return alLeaveRequests.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            var dateFrom = alLeaveRequests.get(position).dateFrom
            var dateTo = alLeaveRequests.get(position).dateTo
            var name = alLeaveRequests.get(position).user!!.name

            val input = SimpleDateFormat("yyyy-MM-dd")
            val output = SimpleDateFormat("MMM dd")
            try {
                var formateStartdate = input.parse(dateFrom)
                var show_start_date = output.format(formateStartdate)

                var formateEnddate = input.parse(dateTo)
                var show_end_date = output.format(formateEnddate)

                holder.tvNameRLR.setText(name)
                if (dateFrom.equals(dateTo)){
                    holder.tvDateRLR.setText(show_start_date)
                }else{
                    holder.tvDateRLR.setText(show_start_date + " - "+show_end_date)
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                Crashlytics.log(e.toString())
            }

            holder.llMainRLR.setOnClickListener {
                var intent = Intent(context, AddLeaveRequestActivity::class.java)
                intent.putExtra("leaveDetail",alLeaveRequests.get(position))
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}