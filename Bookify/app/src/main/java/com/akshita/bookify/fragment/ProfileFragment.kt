package com.akshita.bookify.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.akshita.bookify.R

class ProfileFragment : Fragment() {
    private lateinit var txtUserName: TextView
    private lateinit var txtMobileNum: TextView
    private lateinit var txtEmail: TextView
    private lateinit var txtAddress: TextView
    private lateinit var btnEdit:Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences= (activity as FragmentActivity).getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        btnEdit=view.findViewById(R.id.btn_Edit)
        btnEdit.setOnClickListener {
            val fragment=EditProfileFragment()
            val transaction =
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.FrameLayout, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title ="Edit Details"
        }
        txtUserName=view.findViewById(R.id.txtUserName)
        txtMobileNum=view.findViewById(R.id.txtPhoneNo)
        txtEmail=view.findViewById(R.id.txtEmail)
        txtAddress=view.findViewById(R.id.txtAddress)
        txtUserName.text=sharedPreferences.getString("UserName","Name")
        txtAddress.text=sharedPreferences.getString("Address","Address")
        txtEmail.text=sharedPreferences.getString("Email","Email")
        txtMobileNum.text=sharedPreferences.getString("PhoneNo","Mobile")

        return view
    }
}
