package com.arvi.Adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Model.GetVisitorListResult
import com.arvi.Model.Result
import com.arvi.R
import com.arvi.Utils.GlobalMethods
import kotlinx.android.synthetic.main.row_visitors.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SetVisitorDataAdapter(
    var context: Context,
    var alVisitorList: ArrayList<GetVisitorListResult>,
    var btnlistener: BtnClickListener
) : RecyclerView.Adapter<SetVisitorDataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNameRV = itemView.tvNameRV!!
        var tvMeetPersNameRV = itemView.tvMeetPersNameRV!!
        var tvVisitorDaysRV = itemView.tvVisitorDaysRV!!
        var tvVisitorTimeRV = itemView.tvVisitorTimeRV!!

        var rlVisitorRV = itemView.rlVisitorRV!!
    }


    companion object {
        var mClickListener: BtnClickListener? = null
    }

    open interface BtnClickListener {
        fun onVisitorDetailsBtnClick(position: Int)
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
        return alVisitorList.size
    }

    var lastSelectedPosition: Int = -1
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {

            var itemData = alVisitorList[position]

            holder.tvNameRV.text = itemData.name + "(" + itemData.data!!.company + ")"
            holder.tvMeetPersNameRV.text = "To Meet " + itemData.data!!.visitingTo!!.name

            holder.tvVisitorTimeRV.text =
                GlobalMethods.convertOnlyDate(itemData.data!!.expectedEntryTime!!)
            //GlobalMethods.convertOnlyDate(itemData.data.actualEntry.timeOn)


        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            mClickListener = btnlistener
            holder.rlVisitorRV.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener?.onVisitorDetailsBtnClick(position)
                    lastSelectedPosition = position
                    notifyDataSetChanged()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val format1 = SimpleDateFormat("yyyy-MM-DD hh:mm")
            val dt1: Date = format1.parse(alVisitorList[position].data!!.expectedEntryTime)
            val format2 = SimpleDateFormat("EEEE")
            val finalDay: String = format2.format(dt1)

            holder.tvVisitorDaysRV.text = finalDay

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}