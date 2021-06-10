package com.arvi.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AllDataAttendanceActivity
import com.arvi.Interfaces.AttendanceItemClickListener
import com.arvi.Model.GetCalendarEventsResponseItem
import com.arvi.R
import java.text.SimpleDateFormat

class SetAllDataAdapter(
    var context: Context,
    var alCalendarEvent: ArrayList<GetCalendarEventsResponseItem>,
    var listener: AttendanceItemClickListener
) : RecyclerView.Adapter<SetAllDataAdapter.ViewHolder>() {

    inner class ViewHolder(
        itemView: View,
        listener: AttendanceItemClickListener
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tvDayADR = itemView.findViewById(R.id.tvDayADR) as TextView
        var tvDateADR = itemView.findViewById(R.id.tvDateADR) as TextView
        var tvAbsentADR = itemView.findViewById(R.id.tvAbsentADR) as TextView
        var tvMissedADR = itemView.findViewById(R.id.tvMissedADR) as TextView
        var tvPresentADR = itemView.findViewById(R.id.tvPresentADR) as TextView
        var tvLeaveADR = itemView.findViewById(R.id.tvLeaveADR) as TextView
        var tvVisitorADR = itemView.findViewById(R.id.tvVisitorADR) as TextView
        var tvHolidayADR = itemView.findViewById(R.id.tvHolidayADR) as TextView


        var rlAbsentADR = itemView.findViewById(R.id.rlAbsentADR) as RelativeLayout
        var rlMissedADR = itemView.findViewById(R.id.rlMissedADR) as RelativeLayout
        var rlPresentADR = itemView.findViewById(R.id.rlPresentADR) as RelativeLayout
        var rlLeaveADR = itemView.findViewById(R.id.rlLeaveADR) as RelativeLayout
        var rlVisitorADR = itemView.findViewById(R.id.rlVisitorADR) as RelativeLayout
        var rlHolidayADR = itemView.findViewById(R.id.rlHolidayADR) as RelativeLayout
        var listener: AttendanceItemClickListener?=null
        init {
            this.listener = listener
            rlAbsentADR.setOnClickListener(this)
            rlMissedADR.setOnClickListener(this)
            rlVisitorADR.setOnClickListener(this)
            rlPresentADR.setOnClickListener(this)
            rlHolidayADR.setOnClickListener(this)
            rlLeaveADR.setOnClickListener(this)

        }

        override fun onClick(v: View?) {
            when (v!!.id){
                R.id.rlAbsentADR->{listener!!.onClick(v,adapterPosition,"A" /*alCalendarEvent.get(adapterPosition).A!!.text!!*/)}
                R.id.rlPresentADR->{listener!!.onClick(v,adapterPosition,"P" /*alCalendarEvent.get(adapterPosition).P!!.text!!*/)}
                R.id.rlLeaveADR->{listener!!.onClick(v,adapterPosition,"L" /*alCalendarEvent.get(adapterPosition).L!!.text!!*/)}
                R.id.rlHolidayADR->{listener!!.onClick(v,adapterPosition, "HOL"/*alCalendarEvent.get(adapterPosition).HOL!!.text!!*/)}
                R.id.rlVisitorADR->{listener!!.onClick(v,adapterPosition, "VISITOR"/*alCalendarEvent.get(adapterPosition).VISITOR!!.text!!*/)}
                R.id.rlMissedADR->{listener!!.onClick(v,adapterPosition, "MISS"/*alCalendarEvent.get(adapterPosition).MISS!!.text!!*/)}
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_all_data, parent, false)
        return ViewHolder(v,listener)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return alCalendarEvent.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.tvAbsentADR.setPaintFlags(holder.tvAbsentADR.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            holder.tvMissedADR.setPaintFlags(holder.tvMissedADR.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            holder.tvPresentADR.setPaintFlags(holder.tvPresentADR.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            holder.tvLeaveADR.setPaintFlags(holder.tvPresentADR.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            holder.tvVisitorADR.setPaintFlags(holder.tvPresentADR.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
            holder.tvHolidayADR.setPaintFlags(holder.tvPresentADR.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)

            if(alCalendarEvent.get(position).ABSENT!=null){
                holder.rlAbsentADR.visibility = View.VISIBLE
                holder.tvAbsentADR.setText(alCalendarEvent.get(position).ABSENT!!.text+": "+alCalendarEvent.get(position).ABSENT!!.metric.toString())
            }else{
                holder.rlAbsentADR.visibility = View.GONE
            }

            if(alCalendarEvent.get(position).MISS!=null){
                holder.rlMissedADR.visibility = View.VISIBLE
                holder.tvMissedADR.setText(alCalendarEvent.get(position).MISS!!.text+": "+alCalendarEvent.get(position).MISS!!.metric.toString())
            }else{
                holder.rlMissedADR.visibility = View.GONE
            }

            if(alCalendarEvent.get(position).PRESENT!=null){
                holder.rlPresentADR.visibility = View.VISIBLE
                holder.tvPresentADR.setText(alCalendarEvent.get(position).PRESENT!!.text+": "+alCalendarEvent.get(position).PRESENT!!.metric.toString())
            }else{
                holder.rlPresentADR.visibility = View.GONE
            }

            if(alCalendarEvent.get(position).LATE_CHECKIN!=null){
                holder.rlLeaveADR.visibility = View.VISIBLE
                holder.tvLeaveADR.setText(alCalendarEvent.get(position).LATE_CHECKIN!!.text+": "+alCalendarEvent.get(position).LATE_CHECKIN!!.metric.toString())
            }else{
                holder.rlLeaveADR.visibility = View.GONE
            }

            if(alCalendarEvent.get(position).VISITOR!=null){
                holder.rlVisitorADR.visibility = View.VISIBLE
                holder.tvVisitorADR.setText(alCalendarEvent.get(position).VISITOR!!.text+": "+alCalendarEvent.get(position).VISITOR!!.metric.toString())
            }else{
                holder.rlVisitorADR.visibility = View.GONE
            }

            if(alCalendarEvent.get(position).HOLIDAY!=null){
                holder.rlHolidayADR.visibility = View.VISIBLE
                holder.tvHolidayADR.setText(alCalendarEvent.get(position).HOLIDAY!!.text+": "+alCalendarEvent.get(position).HOLIDAY!!.metric.toString())
            }else{
                holder.rlHolidayADR.visibility = View.GONE
            }


            var strDate = alCalendarEvent.get(position).date
            val outputDate = SimpleDateFormat("dd MMM, yyyy")
            val outputDay = SimpleDateFormat("EEEE")
            val input = SimpleDateFormat("yyyy-MM-dd")
            var formateStartdate = input.parse(strDate)
            var showDate = outputDate.format(formateStartdate)
            var showDay = outputDay.format(formateStartdate)

            holder.tvDateADR.setText(showDate)
            holder.tvDayADR.setText(showDay)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}