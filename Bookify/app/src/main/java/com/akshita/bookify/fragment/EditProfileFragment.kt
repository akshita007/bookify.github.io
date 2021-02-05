package com.akshita.bookify.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.akshita.bookify.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class EditProfileFragment : Fragment() {
    private lateinit var etUserName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhoneNo: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_edit_profile, container, false)
        sharedPreferences= (activity as FragmentActivity).getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE)
        etUserName=view.findViewById(R.id.etUsername)
        etPhoneNo=view.findViewById((R.id.etPhoneNo))
        etEmail=view.findViewById(R.id.etEmail)
        btnSave=view.findViewById(R.id.btn_Save)
        etAddress=view.findViewById(R.id.etAddress)
        auth= Firebase.auth
        btnSave.setOnClickListener {
            val inputUserName = etUserName.text.toString()
            val inputPhoneNo = etPhoneNo.text.toString()
            val inputEmail = etEmail.text.toString()
            val inputAddress = etAddress.text.toString()
            if(!inputPhoneNo.matches("^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}\$".toRegex())){
                Toast.makeText(activity as Context,"Phone number entered is not valid!",Toast.LENGTH_SHORT).show()
            }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()){
                Toast.makeText(activity as Context,"Email entered is not valid!",Toast.LENGTH_SHORT).show()
            }else {
                sharedPreferences.edit().putString("UserName", inputUserName).apply()
                sharedPreferences.edit().putString("PhoneNo", inputPhoneNo).apply()
                sharedPreferences.edit().putString("Email", inputEmail).apply()
                sharedPreferences.edit().putString("Address", inputAddress).apply()

                val fragment = ProfileFragment()
                val args = Bundle()
                args.putString("UserName", inputUserName)
                args.putString("PhoneNo", inputPhoneNo)
                args.putString("Email", inputEmail)
                args.putString("Address", inputAddress)

                fragment.arguments = args
                val transaction =
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.FrameLayout, fragment)
                transaction.commit()
                (context as AppCompatActivity).supportActionBar?.title = "User Profile"
            }
        }
        return view
    }
}