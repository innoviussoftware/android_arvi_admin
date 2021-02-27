package com.arvi.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Model.CompaniesUsersResult
import com.arvi.R
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso

class UserEmployeesListAdapter(
    var context: Context,
    var alComapniesUserList: ArrayList<CompaniesUsersResult>
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
        return alComapniesUserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            var itemData = alComapniesUserList[position]

            if (position / 2 == 0) {
                holder.tvActiveStatusRUED.setText(context.resources.getString(R.string.active_ttl))
                holder.tvActiveStatusRUED.setTextColor(context.resources.getColor(R.color.active_status))
            } else/* if (position / 2 == 0)*/ {
                holder.tvActiveStatusRUED.setText(context.resources.getString(R.string.inactive_ttl))
                holder.tvActiveStatusRUED.setTextColor(context.resources.getColor(R.color.inactive_status))
            }




            holder.tvEmployeeNameRUED.text = itemData!!.name


            val transformation = RoundedTransformationBuilder()
                .cornerRadiusDp(1f)
                .oval(true)
                .build()

            if(itemData.picture !=null) {
                Picasso.with(context)
                    .load(itemData.picture)
                    .fit()
                    .transform(transformation)
                    .into(holder.ivProfileRUED)
            }else{
                holder.ivProfileRUED.setImageDrawable(context.resources.getDrawable(R.drawable.user))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}