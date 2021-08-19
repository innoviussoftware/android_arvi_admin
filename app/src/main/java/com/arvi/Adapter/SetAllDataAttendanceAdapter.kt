package com.arvi.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Model.GetEmpDayDetailResponseItem
import com.arvi.R
import com.arvi.Utils.GlobalMethods
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class SetAllDataAttendanceAdapter(
    var context: Context,
    var alDetails: ArrayList<GetEmpDayDetailResponseItem>
) : RecyclerView.Adapter<SetAllDataAttendanceAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgVwProfileRAD = itemView.findViewById(R.id.imgVwProfileRAD) as CircleImageView
        var tvNameRAD = itemView.findViewById(R.id.tvNameRAD) as TextView
        var imgVwStatusRAD = itemView.findViewById(R.id.imgVwStatusRAD) as CircleImageView
        var tvDateRAD = itemView.findViewById(R.id.tvDateRAD) as TextView
        var tvStartTimeRAD = itemView.findViewById(R.id.tvStartTimeRAD) as TextView
        var tvEndTimeRAD = itemView.findViewById(R.id.tvEndTimeRAD) as TextView
        var tvTotalHrRAD = itemView.findViewById(R.id.tvTotalHrRAD) as TextView
        var llTimeRAD = itemView.findViewById(R.id.llTimeRAD) as LinearLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_all_data_attendance, parent, false)

        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return alDetails.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
/*            if (position  == 0) {
                holder.imgVwStatusRAD.background = context.resources.getDrawable(R.drawable.overtime_status)
            } else if (position / 2 == 0) {
                holder.imgVwStatusRAD.background = context.resources.getDrawable(R.drawable.late_status)
            } else {
                holder.imgVwStatusRAD.background = context.resources.getDrawable(R.drawable.wfh_status)
            }*/
            holder.tvNameRAD.setText(
                alDetails.get(position).emp_name + " - " + alDetails.get(
                    position
                ).emp_id
            )

            if (alDetails.get(position).status.equals("A")) {
                holder.imgVwStatusRAD.setColorFilter(context.resources.getColor(R.color.absent_clr))
            } else if (alDetails.get(position).status.equals("P")) {
                holder.imgVwStatusRAD.setColorFilter(context.resources.getColor(R.color.present_clr))
            } else if (alDetails.get(position).status.equals("MISS")) {
                holder.imgVwStatusRAD.setColorFilter(context.resources.getColor(R.color.miss_clr))
            } else if (alDetails.get(position).status.equals("LA")) {
                holder.imgVwStatusRAD.setColorFilter(context.resources.getColor(R.color.leave_clr))
            } else if (alDetails.get(position).status.equals("HOL")) {
                holder.imgVwStatusRAD.setColorFilter(context.resources.getColor(R.color.holiday_clr))
            } else if (alDetails.get(position).status.equals("VISITOR")) {
                holder.imgVwStatusRAD.setColorFilter(context.resources.getColor(R.color.visitor_clr))
            }

            var strInDate = alDetails.get(position).check_in
            if (strInDate != null) {
                holder.tvStartTimeRAD.text =
                    GlobalMethods.convertUTCDateTimeformate(strInDate)
            }

            var strOutDate = alDetails.get(position).check_out
            if (strOutDate != null) {
                holder.tvEndTimeRAD.text =
                    GlobalMethods.convertUTCDateTimeformate(strOutDate)
            }

            if (strOutDate == null && strInDate == null) {
                holder.llTimeRAD.visibility = View.GONE
            } else {
                holder.llTimeRAD.visibility = View.VISIBLE
            }


            var strTotalWorkHour = alDetails.get(position).workHours
            if (strTotalWorkHour.equals("00:00:00")) {
                holder.tvTotalHrRAD.text = "(00:00)"
            } else if (strTotalWorkHour.equals("0")) {
                holder.tvTotalHrRAD.text = "(00:00)"
            } else {
                holder.tvTotalHrRAD.text =
                    "(" + GlobalMethods.convertHourFormat(strTotalWorkHour) + ")"
            }
            var role = alDetails.get(position).emp_role
            if (role != null) {
                holder.tvDateRAD.setText(role)
            } else {
                holder.tvDateRAD.setText("")
                holder.tvDateRAD.visibility = View.GONE
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}