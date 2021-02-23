package com.arvi.Fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arvi.Activity.NewApp.UserEmployeesListActivity

import com.arvi.R
import com.arvi.Utils.KeyboardUtility
import com.societyguard.Utils.FileUtil
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MultipartBody
import java.io.File


class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var mContext: Context
    var isUpdateOrNot: Boolean = false
    val MY_PERMISSIONS_REQUEST_CAMERA = 1
    var filePathProfile: String? = ""
    var fileProfile: File? = null
    var fileMaltipartProfile: MultipartBody.Part? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        mContext = requireActivity()
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mContext = requireActivity()
        setListners()
    }

    private fun setListners() {
        ivEditProfileFP.setOnClickListener(this)
        rlProfilePicFP.setOnClickListener(this)
        ivChooseProfilePicFP.setOnClickListener(this)
        tvUpdateFP.setOnClickListener(this)
        tvTotalEmployeesFP.setOnClickListener(this)
        ivProfilePicFP.setImageDrawable(mContext.resources.getDrawable(R.drawable.user))

        notUpdateDetails()
    }


    override fun onClick(p0: View?) {


        var i = p0!!.id
        when (i) {
            R.id.tvTotalEmployeesFP -> {
                var intent=Intent(mContext, UserEmployeesListActivity::class.java)
                startActivity(intent)
            }

            R.id.rlProfilePicFP -> {
                try {
                    if (checkPermission()) {
                        CropImage.startPickImageActivity(requireActivity())
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
            R.id.ivChooseProfilePicFP -> {
                try {
                    if (checkPermission()) {
                        CropImage.startPickImageActivity(requireActivity())
                    }
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }

            R.id.ivEditProfileFP -> {
                if (!isUpdateOrNot) {
                    updateDetails()
                } else {
                    notUpdateDetails()
                }
            }
            R.id.tvUpdateFP -> {
                notUpdateDetails()
            }
        }
    }

    private fun updateDetails() {
        isUpdateOrNot = true
        etComapnyNameFP.isEnabled = true
        etAddressFP.isEnabled = true
        etGSTNOFP.isEnabled = true
        etPlanTypeFP.isEnabled = true

        tvUpdateFP.visibility = View.VISIBLE
        rlProfilePicFP.visibility = View.VISIBLE
        ivEditProfileFP.visibility = View.GONE

        KeyboardUtility.showKeyboard(mContext, etComapnyNameFP)

        val expireDate =
            "<font color=#2977DD>" + mContext.resources.getString(R.string.view_list_ttl) + "</font>"
        tvTotalEmployeesFP.text = Html.fromHtml("4 $expireDate")
    }

    private fun notUpdateDetails() {
        isUpdateOrNot = false

        etComapnyNameFP.setText("Test")
        etAddressFP.setText("Test, abd, abd")
        etGSTNOFP.setText("THD1203654789")
        etPlanTypeFP.setText("Stander")

        etComapnyNameFP.isEnabled = false
        etAddressFP.isEnabled = false
        etGSTNOFP.isEnabled = false
        etPlanTypeFP.isEnabled = false

        tvUpdateFP.visibility = View.GONE
        rlProfilePicFP.visibility = View.GONE
        ivEditProfileFP.visibility = View.VISIBLE

        val expireDate =
            "<font color=#2977DD>" + mContext.resources.getString(R.string.view_list_ttl) + "</font>"
        tvTotalEmployeesFP.text = Html.fromHtml("4 $expireDate")
    }

    private fun checkPermission(): Boolean {
        try {

            val accessCamera =
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            val readPermission = ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val writePermission =
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

            val listPermissionsNeeded = ArrayList<String>()

            if (accessCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }
            if (readPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (listPermissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    listPermissionsNeeded.toTypedArray(),
                    MY_PERMISSIONS_REQUEST_CAMERA
                )
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

    private fun startCropImageActivity(imageUri: Uri) {
        CropImage.activity(imageUri).start(requireActivity(), this)
    }

    //Set image .. start
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ContextCompat.checkSelfPermission(
                                    requireActivity(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
/*
                                Toast.makeText(mContext, "Permission accepted.", Toast.LENGTH_LONG)
                                    .show()
*/


                                //  dialogProgress!!.dismiss()
                            }
                        }
                    }

                } else {
                    checkPermission()
                }
            }
        }

    }

    var mCropImageUri: Uri? = null

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    var imageUri: Uri? = null
                    if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                        imageUri = CropImage.getPickImageResultUri(requireActivity(), data)
                        if (CropImage.isReadExternalStoragePermissionsRequired(
                                requireActivity(),
                                imageUri
                            )
                        ) {
                            mCropImageUri = imageUri
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                                ), CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE
                            )
                        } else {
                            startCropImageActivity(imageUri)
                        }
                    } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                        try {
                            val uri = CropImage.getActivityResult(data).getUri()

                            //Todo: isSelectImageType=0 means user pic, isSelectImageType=1 means company pic
                            filePathProfile = FileUtil.getPath(mContext!!, uri)

                            ivProfilePicFP.setImageURI(uri)
                            /* val transformation = RoundedTransformationBuilder()
                                 .borderColor(Color.GRAY)
                                 .borderWidthDp(1f)
                                 .oval(true)
                                 .build()

                             Picasso.with(mContext)
                                 .load(uri)
                                 .fit()
                                 .transform(transformation)
                                 .into(ivProfilePicAEP)*/

                        } catch (e: Exception) {
                            e.printStackTrace();
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
