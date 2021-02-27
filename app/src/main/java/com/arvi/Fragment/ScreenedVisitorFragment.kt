package com.arvi.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arvi.Activity.NewApp.AddVisitorDetailActivity
import com.arvi.Activity.NewApp.EnterCompanyDetailActivity
import com.arvi.Adapter.SetVisitorDataAdapter
import com.arvi.Model.Result
import com.arvi.Model.VisitorsListModel
import com.arvi.R
import com.arvi.RetrofitApiCall.APIService
import com.arvi.RetrofitApiCall.ApiUtils
import com.arvi.SessionManager.SessionManager
import com.arvi.Utils.AppConstants
import com.arvi.Utils.ConnectivityDetector
import com.arvi.Utils.MyProgressDialog
import com.arvi.Utils.SnackBar
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScreenedVisitorFragment : Fragment(), View.OnClickListener {

    var rVwVisitorVLF: RecyclerView? = null
var tvNoVisitorVLF: TextView? = null
var imgVwAddVisitorVLF: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_screened_visitor, container, false)

        setIds(view)
        setListeners()

        if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
            callGetVisitorListAPI()
        } else {
            SnackBar.showInternetError(appContext!!, snackbarView!!)
        }
        return view
    }

    var appContext: Context? = null
    var snackbarView: View? = null

    companion object {
        @JvmStatic
        fun newInstance() = ScreenedVisitorFragment()
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


    private fun setListeners() {
        try {
            imgVwAddVisitorVLF!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v!!.id) {
                R.id.imgVwAddVisitorVLF -> {
                    var intent = Intent(appContext, AddVisitorDetailActivity::class.java)
                    startActivityForResult(intent,REQUEST_VISITOR)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    var totalPageSize: Int = 0
    var currentPage: Int = 1

    lateinit var alVisitorList: ArrayList<Result>

    private fun callGetVisitorListAPI() {
        try {

            var token = AppConstants.BEARER_TOKEN + SessionManager.getToken(appContext!!)

            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            MyProgressDialog.showProgressDialog(appContext!!)

            mAPIService!!.getVisitorsList("application/json", token/*, totalPageSize, currentPage*/)
                .enqueue(object : Callback<VisitorsListModel> {
                    override fun onResponse(
                        call: Call<VisitorsListModel>,
                        response: Response<VisitorsListModel>
                    ) {
                        MyProgressDialog.hideProgressDialog()
                        try {
                            if (response.code() == 200) {
                                alVisitorList = ArrayList()
                                alVisitorList.addAll(response.body().result!!)
                                setVisitorData(alVisitorList)
                            } else if (response.code() == 401) {
                                var intent = Intent(appContext!!, EnterCompanyDetailActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                SessionManager.clearAppSession(appContext!!)
                            }else {
                                SnackBar.showError(
                                    context!!,
                                    snackbarView!!,
                                    "Something went wrong"
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }

                    override fun onFailure(
                        call: Call<VisitorsListModel>,
                        t: Throwable
                    ) {
                        MyProgressDialog.hideProgressDialog()
                    }
                })

        } catch (e: Exception) {
            e.printStackTrace()
            MyProgressDialog.hideProgressDialog()

        }
    }


    public  val REQUEST_VISITOR=1577
    private fun setVisitorData(alVisitorList: ArrayList<Result>) {
        try {

            if (alVisitorList.size > 0) {
                rVwVisitorVLF!!.visibility=View.VISIBLE
                tvNoVisitorVLF!!.visibility=View.GONE


                var setVisitorDataAdapter = SetVisitorDataAdapter(appContext!!, alVisitorList,
                    object : SetVisitorDataAdapter.BtnClickListener {
                        override fun onVisitorDetailsBtnClick(position: Int) {
                            val gson = Gson()
                            var myJson = gson.toJson(alVisitorList[position])

                            var intent = Intent(context, AddVisitorDetailActivity::class.java)
                            intent.putExtra("from", "list")
                            intent.putExtra("visitorData",myJson)
                            startActivityForResult(intent,REQUEST_VISITOR)
                        }
                    })

                rVwVisitorVLF!!.layoutManager =
                    LinearLayoutManager(appContext, RecyclerView.VERTICAL, false)
                rVwVisitorVLF!!.adapter = setVisitorDataAdapter
            }
            else{
                rVwVisitorVLF!!.visibility=View.GONE
                tvNoVisitorVLF!!.visibility=View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==REQUEST_VISITOR){
            if (ConnectivityDetector.isConnectingToInternet(appContext!!)) {
                callGetVisitorListAPI()
            } else {
                SnackBar.showInternetError(appContext!!, snackbarView!!)
            }
        }
    }

}