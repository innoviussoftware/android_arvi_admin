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
    var alVisitorList: ArrayList<GetVisitorListResult>, var isScreenedExpected: Int,
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

            holder.tvNameRV.text = itemData.data.visitor.name + "(" + itemData.data!!.comingFrom + ")"

            holder.tvMeetPersNameRV.text = "To Meet " + itemData.data!!.visitingTo!!.name

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
            if (alVisitorList[position].data!!.expectedEntryTime != null) {
                if (alVisitorList[position].data!!.expectedEntryTime!!.contains("Z")) {
                    holder.tvVisitorTimeRV.text =
                        GlobalMethods.convertUTCDateformate(alVisitorList[position].data!!.expectedEntryTime!!)
                } else {
                    holder.tvVisitorTimeRV.text =
                        GlobalMethods.convertOnlyDate(alVisitorList[position].data!!.expectedEntryTime!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

       /* try {
            val format1 = SimpleDateFormat("yyyy-MM-DD'T'hh:mm:ssz")
            val dt1: Date = format1.parse(alVisitorList[position].data!!.expectedEntryTime!!)
            val format2 = SimpleDateFormat("MMM dd")
            val finalDay: String = format2.format(dt1)

            holder.tvVisitorTimeRV.text = finalDay
        } catch (e: Exception) {
            e.printStackTrace()
        }
*/
        try {
            if (alVisitorList[position].data!!.expectedEntryTime != null) {

                if (alVisitorList[position].data!!.expectedEntryTime!!.contains("Z")) {

                    val format1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.s'Z'")
                    val dt1: Date = format1.parse(alVisitorList[position].data!!.expectedEntryTime)
                    val format2 = SimpleDateFormat("EEEE")
                    val finalDay: String = format2.format(dt1)

                    holder.tvVisitorDaysRV.text = finalDay
                } else {
                    val format1 = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val dt1: Date = format1.parse(alVisitorList[position].data!!.expectedEntryTime)
                    val format2 = SimpleDateFormat("EEEE")
                    val finalDay: String = format2.format(dt1)

                    holder.tvVisitorDaysRV.text = finalDay
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}