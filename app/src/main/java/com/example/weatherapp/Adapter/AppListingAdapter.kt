package com.example.weatherapp.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.AppConstants
import com.example.weatherapp.Model.AppModel
import com.example.weatherapp.R


class AppListingAdapter(private val mDataList: ArrayList<AppModel>, private val ctx: Context) :
    RecyclerView.Adapter<AppListingAdapter.MyViewHolder>() {

    private val connectionManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectionManager.activeNetworkInfo
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_listing_card_layout, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        setAppInfo(mDataList[position])
        if (mDataList[position].icon != null) {
            holder.ivIcon.setImageDrawable(mDataList[position].icon)
        } else {
            holder.ivIcon.setImageDrawable(ctx.getDrawable(R.drawable.ic_icon))
        }
        holder.tvAppName.text = mDataList[position].appName
        holder.tvStatus.text = mDataList[position].appStatus
        holder.tvStatus.tag = mDataList[position].packageName
        holder.tvStatus.setOnClickListener {
                val btn = it as Button
                val buttonText = btn.text.toString()
                val appPackage: String = it.getTag() as String
                if (buttonText.equals(AppConstants.INSTALL)) {
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(AppConstants.PLAY_STORE_URL+appPackage)
                    ctx.startActivity(i)
                } else {
                    val launchIntent: Intent? = ctx.packageManager.getLaunchIntentForPackage(appPackage)
                    if (launchIntent != null) {
                        ctx.startActivity(launchIntent)
                    } else {
                        Toast.makeText(
                            ctx, AppConstants.NO_PACKAGE_MSG,
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var ivIcon: ImageView = itemView.findViewById<View>(R.id.ivAppIcon) as ImageView
        internal var tvAppName: TextView = itemView.findViewById<View>(R.id.tvAppName) as TextView
        internal var tvStatus: Button = itemView.findViewById<View>(R.id.tvStatus) as Button

    }

    private fun setAppInfo(model: AppModel) {
        try {
            val app: ApplicationInfo = ctx.packageManager.getApplicationInfo(model.packageName, 0)
            val icon: Drawable = ctx.packageManager.getApplicationIcon(app)
            val name: String = ctx.packageManager.getApplicationLabel(app).toString()
            model.appName = name
            model.icon = icon
            model.appStatus = AppConstants.OPEN
        } catch (e: PackageManager.NameNotFoundException) {
            model.appStatus = AppConstants.INSTALL
            e.printStackTrace()
        }
    }
}