package com.arvi.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Interfaces.RecyclerViewItemClicked
import com.arvi.Model.GetRegularisationRequestResponseItem
import com.arvi.R
import java.text.SimpleDateFormat

class SetRegularizationDataAdapter(
    var context: Context,
    var alRegularisation: ArrayList<GetRegularisationRequestResponseItem>,
    var listener: RecyclerViewItemClicked
) : RecyclerView.Adapter<SetRegularizationDataAdapter.ViewHolder>() {

    inner class ViewHolder(
        itemView: View,
        listener: RecyclerViewItemClicked
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var rlVisitorRV = itemView.findViewById(R.id.rlVisitorRV) as RelativeLayout
        var tvNameRR = itemView.findViewById(R.id.tvNameRR) as TextView
        var tvDateRR = itemView.findViewById(R.id.tvDateRR) as TextView
        var tvStatusRR = itemView.findViewById(R.id.tvStatusRR) as TextView
        var tvStartTimeRR = itemView.findViewById(R.id.tvStartTimeRR) as TextView
        var tvEndTimeRR = itemView.findViewById(R.id.tvEndTimeRR) as TextView
        var llTimeRR = itemView.findViewById(R.id.llTimeRR) as LinearLayout
        lateinit var listener: RecyclerViewItemClicked
        init {
            this.listener = listener
            rlVisitorRV.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            when(view!!.id){
                R.id.rlVisitorRV->{
                    listener.onClick(view,adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_regularization, parent, false)
        return ViewHolder(v,listener)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return alRegularisation.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.tvNameRR.setText(alRegularisation.get(position).user!!.name)

            var strdate = alRegularisation.get(position).dateOn
            val input = SimpleDateFormat("yyyy-MM-dd")
            val output = SimpleDateFormat("MMM dd")
            var formateStartdate = input.parse(strdate)
            var show_date = output.format(formateStartdate)
            holder.tvDateRR.setText(show_date)

            var status = alRegularisation.get(position).status
            /*P,A,MISS,LA,OT,HOL */
            var showStatus = ""
            var statuslist = status!!.split("|")
            for (i in statuslist.indices)
            {
                if (statuslist.get(i).equals("P")){
                    showStatus = showStatus+" "+"Present"
                }else if (statuslist.get(i).equals("A")){
                    showStatus = showStatus+" "+"Absent"
                }else if (statuslist.get(i).equals("HOL")){
                    showStatus = showStatus+" "+"Holiday"
                }else if (statuslist.get(i).equals("LA")){
                    showStatus = showStatus+" "+"Late"
                }else if (statuslist.get(i).equals("MISS")){
                    showStatus = showStatus+" "+"Miss"
                }else if (statuslist.get(i).equals("OT")){
                    showStatus = showStatus+" "+"Overtime"
                }else if (statuslist.get(i).equals("L")){
                    showStatus = showStatus+" "+"Leave"
                }
            }
            showStatus = showStatus.substring(1,showStatus.length)
            holder.tvStatusRR.setText(showStatus)

            if(showStatus.equals("Absent") || showStatus.equals("Holiday")){
                holder.llTimeRR.visibility = View.GONE
            }

            if (alRegularisation.get(position).inAt!=null){
                var inDateTime = alRegularisation.get(position).inAt
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'")
                val output = SimpleDateFormat("hh:mm a")
                var formateStartdate = input.parse(inDateTime)
                var showInTime = output.format(formateStartdate)
                holder.tvStartTimeRR.setText(showInTime)
            }

            if (alRegularisation.get(position).outAt!=null){
                var outDateTime = alRegularisation.get(position).outAt
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'")
                val output = SimpleDateFormat("hh:mm a")
                var formateEnddate = input.parse(outDateTime)
                var showOutTime = output.format(formateEnddate)
                holder.tvEndTimeRR.setText(showOutTime)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}