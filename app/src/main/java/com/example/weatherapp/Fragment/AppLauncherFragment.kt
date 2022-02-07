package com.example.weatherapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.Model.AppModel
import com.example.weatherapp.R
import com.example.weatherapp.Adapter.AppListingAdapter

class AppLauncherFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    var listOfApps: ArrayList<AppModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_launcher, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val appList1 = AppModel(null, "Whatsapp", "com.whatsapp", "install")
        val appList2 = AppModel(null, "Gmail", "com.google.android.gm", "install")
        val appList3 = AppModel(null, "YouTube", "com.google.android.youtube", "install")
        val appList4 = AppModel(null, "Indeed", "com.indeed.android.jobsearch", "install")
        val appList5 = AppModel(null, "Zoom", "us.zoom.videomeetings", "install")
        val appList6 = AppModel(null, "Bolt", "ee.mtakso.driver", "install")
        val appList7 = AppModel(null, "Facebook", "com.facebook.katana", "install")
        val appList8 = AppModel(null, "Netflix", "com.netflix.mediaclient", "install")
        val appList9 = AppModel(null, "FreeNow", "taxi.android.client", "install")
        listOfApps.add(appList1)
        listOfApps.add(appList2)
        listOfApps.add(appList3)
        listOfApps.add(appList4)
        listOfApps.add(appList5)
        listOfApps.add(appList6)
        listOfApps.add(appList7)
        listOfApps.add(appList8)
        listOfApps.add(appList9)

        mRecyclerView = view?.findViewById(R.id.rvAppListing)
        val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView!!.layoutManager = mLayoutManager
    }

    override fun onResume() {
        super.onResume()
        mAdapter = AppListingAdapter(listOfApps, requireContext())
        mRecyclerView!!.adapter = mAdapter
    }
}


