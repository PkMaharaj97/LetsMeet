@file:Suppress("DEPRECATION")

package com.example.letsmeet.startup
import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.letsmeet.MainActivity
import com.example.letsmeet.R
import com.example.letsmeet.addevent.ApiService

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("DEPRECATION")
class Login: AppCompatActivity() {

   private var edtEmail: EditText? = null
    private   var edtPassword: EditText? = null
    private  var chkPassword: CheckBox? = null
    private  var txtForgerPswd: TextView? = null
    private  var txtNewuser: TextView? = null
    private  var btnLogin: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()
        bindvariables()
        setListeners()


    }

    private fun bindvariables() {
        edtEmail = findViewById(R.id.login_emailid)
        edtPassword = findViewById(R.id.login_password)
        chkPassword = findViewById(R.id.show_hide_password)
        txtForgerPswd = findViewById(R.id.forgot_password)
        txtNewuser = findViewById(R.id.createAccount)
        btnLogin = findViewById(R.id.loginBtn)

    }

    private fun setListeners() {
        txtNewuser!!.setOnClickListener {
            val i = Intent(this, Signup::class.java)
            startActivity(i)
        }
        txtForgerPswd!!.setOnClickListener {
            val j = Intent(this, MainActivity::class.java)
            startActivity(j)
        }

        chkPassword!!.setOnClickListener { pswdactivity() }
        btnLogin!!.setOnClickListener {
            if (loginValidation()) {
                datavalidation()
            }
        }

        /*  override fun onClick(v: View?)
          {

          }*/


    }

    private   fun pswdactivity() {
        val checkBox = findViewById<CheckBox>(R.id.show_hide_password)
        checkBox?.setOnCheckedChangeListener { _, isChecked ->
            (if (isChecked) {
                edtPassword?.inputType = InputType.TYPE_CLASS_TEXT
                chkPassword?.setText(R.string.hide_pwd)
            } else {
                edtPassword?.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                chkPassword?.setText(R.string.show_pwd)

            })
        }

    }

    private  fun loginValidation(): Boolean {//var icon = AppCompatResources.getDrawable(this, R.drawable.warning)
        var bolean= true
        var email = edtEmail?.text.toString()
        var password = edtPassword?.text.toString()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                .matches())
        { //Toast.makeText(this,"Enter valid email",Toast.LENGTH_SHORT).show()
            edtEmail?.setError("Enter valid email")
            edtEmail?.requestFocus()
            bolean = false
        }
        else if (password.isEmpty()) {
            edtPassword?.setError("Enter password")
            edtPassword?.requestFocus()
            bolean = false
        }
        return bolean
    }
    fun datavalidation()
    { var Email=edtEmail?.text.toString()
        var Password=edtPassword?.text.toString()

        ApiService.loginApiCall().doLogin(Email,Password)
            .enqueue(object : Callback<Message> {
                override fun onResponse(call: Call<Message>, response: Response<Message>)
                {
                    Log.e("pkResponse::::", response.body().toString())
                    val signResponse = response.body()!!
                    if (signResponse.status)
                    {
                        Toast.makeText(applicationContext, "Successful login", Toast.LENGTH_LONG).show()
                        val userinfo=signResponse.data
                         addShare(userinfo)
                        val i = Intent(applicationContext, MainActivity::class.java)
                        startActivity(i)
                        finish()

                    }else{
                        Toast.makeText(applicationContext, response.body()!!.message, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Message>, t: Throwable)
                {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    Log.e("pkonfailure",t.message)



                }

            })

         }

    fun addShare(user:Users)
    {
        val sharedPreferences: SharedPreferences = getDefaultSharedPreferences(this)

        val editor = sharedPreferences.edit()
        //2
        editor.putString("Name", user.Name)
        editor.putString("UEmail", user.Email)
        editor.putString("UPhone", user.Phone)
        editor.putString("UPassword", user.Password)
        editor.putString("UId", user.Id)
              editor.apply()
        Toast.makeText(this,"Name:"+sharedPreferences.getString("Name","11"),Toast.LENGTH_SHORT).show()

    }


}
