package com.arvi.Utils

import android.R
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.jvm.Throws

class GlobalMethods {

    //Todo: Create Global Method as per using all project
    companion object {

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.activeNetworkInfo.also {
                return it != null && it.isConnected
            }
        }

        fun isEmailValid(email: String): Boolean {
            return Pattern.compile(
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-].+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            ).matcher(email).matches()
        }

        fun isPasswordValid(email: String): Boolean {
            //^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$
            return Pattern.compile(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{6,}\$"
            ).matcher(email).matches()

        }

        fun displayCurrencyInfoForLocale(locale: Locale): Currency {
            System.out.println("Locale: " + locale.displayName)
            val currency = Currency.getInstance(locale)
            System.out.println("Currency Code: " + currency.currencyCode)
            System.out.println("Symbol: " + currency.symbol)
            System.out.println("Default Fraction Digits: " + currency.defaultFractionDigits)
            return currency
        }

        fun convertDate(sendDate: String): String {
            var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val newsDate = sendDate
            var formatedDate: String? = ""
            if (newsDate != null) {
                val newDate = format.parse(newsDate)
                format = SimpleDateFormat("MMM dd, yyyy")
                formatedDate = format.format(newDate)

            }

            return formatedDate!!
        }

        fun convertMonthYear(sendDate: String): String {
            var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val newsDate = sendDate
            var formatedDate: String? = ""
            if (newsDate != null) {
                val newDate = format.parse(newsDate)
                format = SimpleDateFormat("MMM, yyyy")
                formatedDate = format.format(newDate)

            }

            return formatedDate!!
        }


        fun convertOnlyDate(sendDate: String): String {
            var format = SimpleDateFormat("yyyy-MM-dd hh:mm")
            val newsDate = sendDate
            var formatedDate: String? = ""
            if (newsDate != null) {
                val newDate = format.parse(newsDate)
                format = SimpleDateFormat("MMM dd")
                formatedDate = format.format(newDate)
            }
            return formatedDate!!
        }

        fun convertOnlySendDate(sendDate: String): String {
            var format = SimpleDateFormat("yyyy-MM-dd")
            val newsDate = sendDate
            var formatedDate: String? = ""
            if (newsDate != null) {
                val newDate = format.parse(newsDate)
                format = SimpleDateFormat("MMM dd, yyyy")
                formatedDate = format.format(newDate)
            }
            return formatedDate!!
        }

        fun convertTime(sendTime: String): String {
            var format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val newsDate = sendTime
            var formatedDate: String? = ""
            if (newsDate != null) {
                val newDate = format.parse(newsDate)
                format = SimpleDateFormat("hh:mm aa")
                formatedDate = format.format(newDate)

            }

            return formatedDate!!
        }

        fun getAddressUsingLocation(
            mContext: Context,
            latitude: Double,
            longitude: Double
        ): String {
            var userAddress: String = ""

            try {
                val geocoder: Geocoder
                val addresses: List<Address>
                geocoder = Geocoder(mContext, Locale.getDefault())

                addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                val address: String = addresses[0]
                    .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                val city: String = addresses[0].getLocality()
                val state: String = addresses[0].getAdminArea()
                val country: String = addresses[0].getCountryName()
                val postalCode: String = addresses[0].getPostalCode()
                val knownName: String = addresses[0].getFeatureName()


                userAddress = address + "," + city + "," + postalCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return userAddress
        }


        @Throws(ParseException::class)
        fun formatToYesterdayOrToday(date: String): String {
            val dateTime = SimpleDateFormat("EEE hh:mma MMM d, yyyy").parse(date)
            val calendar = Calendar.getInstance()
            calendar.setTime(dateTime)
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            return if (calendar.get(Calendar.YEAR) === today.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) === today.get(Calendar.DAY_OF_YEAR)
            ) {
                "Today " + timeFormatter.format(dateTime)
            } else if (calendar.get(Calendar.YEAR) === yesterday.get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) === yesterday.get(Calendar.DAY_OF_YEAR)
            ) {
                "Yesterday " + timeFormatter.format(dateTime)
            } else {
                date
            }
        }

        fun getDisplayableTimeUsingDate(selectedDate: String): String? {

            val formatter: DateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date = formatter.parse(selectedDate) as Date
            System.out.println("Today is " + date.time)

            var timestamp: Long = date.time

            var difference: Long = 0
            val mDate = System.currentTimeMillis()
            if (mDate > timestamp) {
                difference = mDate - timestamp
                val seconds = difference / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val months = days / 31
                val years = days / 365
                return if (seconds < 0) {
                    "not yet"
                } else if (seconds < 60) {
                    if (seconds == 1L) "one second ago" else "$seconds seconds ago"
                } else if (seconds < 120) {
                    // "a minute ago"
                    "$minutes minutes ago"
                } else if (seconds < 2700) // 45 * 60
                {
                    "$minutes minutes ago"
                } else if (seconds < 5400) // 90 * 60
                {
                    // "an hour ago"
                    "$hours hours ago"
                } else if (seconds < 86400) // 24 * 60 * 60
                {
                    "$hours hours ago"
                } else if (seconds < 172800) // 48 * 60 * 60
                {
                    "yesterday"
                } else if (seconds < 2592000) // 30 * 24 * 60 * 60
                {
                    "$days days ago"
                } else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
                {
                    if (months <= 1) "one month ago" else "$days months ago"
                } else {
                    if (years <= 1) "one year ago" else "$years years ago"
                }
            }
            return null
        }

        fun getDisplayableTime(timestamp: Long): String? {

            var difference: Long = 0
            val mDate = System.currentTimeMillis()
            if (mDate > timestamp) {
                difference = mDate - timestamp
                val seconds = difference / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val months = days / 31
                val years = days / 365
                return if (seconds < 0) {
                    "not yet"
                } else if (seconds < 60) {
                    if (seconds == 1L) "one second ago" else "$seconds seconds ago"
                } else if (seconds < 120) {
                    // "a minute ago"
                    "$minutes minutes ago"
                } else if (seconds < 2700) // 45 * 60
                {
                    "$minutes minutes ago"
                } else if (seconds < 5400) // 90 * 60
                {
                    // "an hour ago"
                    "$hours hours ago"
                } else if (seconds < 86400) // 24 * 60 * 60
                {
                    "$hours hours ago"
                } else if (seconds < 172800) // 48 * 60 * 60
                {
                    "yesterday"
                } else if (seconds < 2592000) // 30 * 24 * 60 * 60
                {
                    "$days days ago"
                } else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
                {
                    if (months <= 1) "one month ago" else "$days months ago"
                } else {
                    if (years <= 1) "one year ago" else "$years years ago"
                }
            }
            return null
        }

        public fun getAge(dobString: String): Int {
            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            try {
                date = sdf.parse(dobString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (date == null) return 0
            val dob = Calendar.getInstance()
            val today = Calendar.getInstance()
            dob.time = date
            val year = dob[Calendar.YEAR]
            val month = dob[Calendar.MONTH]
            val day = dob[Calendar.DAY_OF_MONTH]
            dob[year, month + 1] = day
            var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
            if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
                age--
            }
            return age
        }

        fun showConfirmationAlert(title: String?, message: String?, mContext: Context): Boolean {
            //hideProgress()
            var isYesNo: Boolean = false
            var alert = AlertDialog.Builder(mContext)
            alert.setTitle(title).setMessage(message)
                .setPositiveButton(R.string.yes) { dialog, which ->
                    isYesNo = true
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
            try {
                alert.show()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return isYesNo
        }


        fun printHashKey(pContext: Context): String {
            var hashKey: String? = ""
            try {
                val info: PackageInfo = pContext.packageManager.getPackageInfo(
                    pContext.packageName,
                    PackageManager.GET_SIGNATURES
                )
                for (signature in info.signatures) {
                    val md: MessageDigest = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    hashKey = String(Base64.encode(md.digest(), 0))
                    Log.i("TAG", "printHashKey() Hash Key: $hashKey")
                }
            } catch (e: NoSuchAlgorithmException) {
                Log.e("TAG", "printHashKey()", e)
            } catch (e: java.lang.Exception) {
                Log.e("TAG", "printHashKey()", e)
            }

            return hashKey!!
        }

    }
}