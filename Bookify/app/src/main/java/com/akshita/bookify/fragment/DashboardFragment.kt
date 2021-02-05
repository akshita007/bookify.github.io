package com.akshita.bookify.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akshita.bookify.R
import com.akshita.bookify.adapter.DashboardAdapterClass
import com.akshita.bookify.model.VolInfo
import com.akshita.bookify.util.NetworkManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [DashboardFragment] factory method to
 * create an instance of this fragment.
 */
class DashboardFragment : Fragment() {
    private lateinit var recyclerView:RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar:ProgressBar
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter:DashboardAdapterClass
//    var amount: Double? = null
    //private lateinit var queryParameter:String
    var bookList= arrayListOf<VolInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_dashboard, container, false)
        recyclerView=view.findViewById(R.id.RecyclerView)
        progressLayout=view.findViewById(R.id.ProgressLayout)
        progressBar=view.findViewById(R.id.ProgressBar)
        progressLayout.visibility=View.VISIBLE
        layoutManager=LinearLayoutManager(activity)
        setHasOptionsMenu(true)
        fetchBooks("Wallflower")
        return view
    }
    fun fetchBooks(queryParameter:String){
        val queue = Volley.newRequestQueue(activity as Context)
        val url="https://www.googleapis.com/books/v1/volumes?q='$queryParameter'&maxResults=40&filter=paid-ebooks"
        if (NetworkManager().checkConnectivity(activity as Context)){
            //dashboard fragment code
            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url,null,Response.Listener {
                    response: JSONObject ->
                try {
                    progressLayout.visibility=View.GONE
                    val data = response.getJSONArray("items")
                    for (i in 0 until data.length()) {
                        val bookItemObject = data.getJSONObject(i)
                        val bookVolInfo = bookItemObject.getJSONObject("volumeInfo")
                        try {
                            val book = VolInfo(
                                bookItemObject.getString("id"),
                                bookVolInfo.getString("title"),
                                bookVolInfo.getJSONArray("authors"),
                                bookVolInfo.getString("publisher"),
                                bookVolInfo.getString("description"),
                                bookVolInfo.getInt("pageCount"),
                                bookVolInfo.getJSONArray("categories"),
                                bookVolInfo.getDouble("averageRating"),
                                bookVolInfo.getJSONObject("imageLinks").getString("thumbnail"),
                                bookVolInfo.getString("language"),
                                bookItemObject.getJSONObject("saleInfo").getJSONObject("retailPrice").getDouble("amount"),
                                bookItemObject.getJSONObject("saleInfo").getString("buyLink"),
                            )
                            bookList.add(book)
                            recyclerAdapter =
                                DashboardAdapterClass(activity as Context, bookList)
                            layoutManager = LinearLayoutManager(activity as Context)
                            recyclerView.adapter = recyclerAdapter
                            recyclerView.layoutManager = layoutManager
                            recyclerAdapter.notifyDataSetChanged()
                        }catch(e:Exception){
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    Toast.makeText(
                        activity as Context,
                        " Some error has occurred!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
                Response.ErrorListener {
                    //handles error
                    if (activity != null)
                        Toast.makeText(
                            activity as Context,
                            "Some error has occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"]="application/json"
                    headers["x-api-key"] = "AIzaSyCY58Cxl-rnSZwa4QqKBnWrLIG9xoSWzz4"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else{
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
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        val searchItem=menu.findItem(R.id.search_ic)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        bookList.clear()
                        fetchBooks(query.toString())
                    }else{
                        bookList.clear()
                        fetchBooks("wallflower")
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        bookList.clear()
                        fetchBooks(newText)
                    }else{
                        bookList.clear()
                        fetchBooks("wallflower")
                    }
                    return true
                }
            })
        }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if (id == R.id.search_ic) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}