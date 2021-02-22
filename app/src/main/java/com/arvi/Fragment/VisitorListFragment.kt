package com.arvi.Fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AddVisitorDetailActivity
import com.arvi.Adapter.SetVisitorDataAdapter

import com.arvi.R
import com.arvi.Utils.SnackBar
import com.google.android.material.snackbar.Snackbar


class VisitorListFragment : Fragment(), View.OnClickListener {


    var rVwVisitorVLF: RecyclerView? = null
    var tvNoVisitorVLF: TextView? = null
    var imgVwAddVisitorVLF: ImageView? = null

    var appContext: Context? = null
    var snackbarView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_visitor_list, container, false)
        try {
            setIds(view)
            setListeners()
            setVisitorData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }

    @SuppressLint("WrongConstant")
    private fun setVisitorData() {
        try {
            var setVisitorDataAdapter = SetVisitorDataAdapter(appContext!!)
            rVwVisitorVLF!!.layoutManager =
                LinearLayoutManager(appContext, LinearLayout.VERTICAL, false)
            rVwVisitorVLF!!.setAdapter(setVisitorDataAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setListeners() {
        try {
            imgVwAddVisitorVLF!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIds(view: View) {
        try {
            appContext = activity
            snackbarView = activity?.findViewById(android.R.id.content)
            rVwVisitorVLF = view.findViewById(R.id.rVwVisitorVLF)
            tvNoVisitorVLF = view.findViewById(R.id.tvNoVisitorVLF)
            imgVwAddVisitorVLF = view.findViewById(R.id.imgVwAddVisitorVLF)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v!!.id) {
                R.id.imgVwAddVisitorVLF -> {
                    var intent = Intent(appContext, AddVisitorDetailActivity::class.java)
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VisitorListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
