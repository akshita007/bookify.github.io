package com.akshita.bookify.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.akshita.bookify.R
import com.akshita.bookify.adapter.FavAdapterClass
import com.akshita.bookify.database.BookDatabase
import com.akshita.bookify.database.BookEntities
import com.akshita.bookify.util.NetworkManager


class FavsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: FavAdapterClass
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private var dbResList = listOf<BookEntities>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favs, container, false)
        recyclerView = view.findViewById(R.id.RecyclerView)
        layoutManager = LinearLayoutManager(activity)
        progressLayout=view.findViewById(R.id.ProgressLayout)
        progressBar=view.findViewById(R.id.ProgressBar)
        progressLayout.visibility=View.VISIBLE
        recyclerAdapter = FavAdapterClass(activity as Context, dbResList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter
        if (NetworkManager().checkConnectivity(activity as Context)) {
            //Fav fragment code
            dbResList = RetrieveData(activity as Context).execute().get()
            if (activity != null) {
            progressLayout.visibility=View.GONE
                recyclerAdapter = FavAdapterClass(activity as Context, dbResList)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = recyclerAdapter
            }
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setMessage("No Internet connection found").setTitle("OOPS!")
            dialog.setPositiveButton("Open settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    //retrieves data  for fav fragment
    class RetrieveData(val context: Context) : AsyncTask<Void, Void, List<BookEntities>>() {
        override fun doInBackground(vararg params: Void?): List<BookEntities> {
            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()
            return db.bookDao().getAllBooks()
        }
    }
}
