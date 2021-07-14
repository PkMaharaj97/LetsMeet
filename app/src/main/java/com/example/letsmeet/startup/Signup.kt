package com.example.letsmeet.startup
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.letsmeet.R
import com.example.letsmeet.addevent.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Signup:AppCompatActivity()
{   private   var edtName: EditText? = null
    private  var edtEmailid: EditText? = null
    private  var edtPhone: EditText? = null
    private  var edtLocation: EditText? = null
    private  var edtPswd1: EditText? = null
    private  var edtPswd2: EditText? = null
    private  var chkTc:CheckBox? = null
    private  var txtLoginhere:TextView?= null
    private  var btnSignup:Button? = null

    override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.signup_layout)
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
      actionBar?.hide()

      supportActionBar?.hide()
      bindvariables()
      setListeners()
  }
private fun bindvariables()
{
    edtName = findViewById(R.id.fullName)
    edtEmailid = findViewById(R.id.userEmailId)
    edtPhone = findViewById(R.id.mobileNumber)
    edtLocation=findViewById(R.id.location)
    edtPswd1 = findViewById(R.id.password)
    edtPswd2 = findViewById(R.id.confirmPassword)
    chkTc = findViewById(R.id.terms_conditions)
    txtLoginhere = findViewById(R.id.already_user)
    btnSignup = findViewById(R.id.signUpBtn)
}
private fun setListeners()
{
    txtLoginhere!!.setOnClickListener {
        val i = Intent(this, Login::class.java)
        startActivity(i)
        }
        btnSignup!!.setOnClickListener {
            if (signupValidation()) {
                datavalidation()
            }

        }
    chkTc!!.setOnClickListener { pswdactivity() }

}
private fun signupValidation():Boolean
{ var boolean: Boolean = true
    var email = edtEmailid?.text.toString()
    var name = edtName?.text.toString()
    var phoneNo = edtPhone?.text.toString()
    var location = edtLocation?.text.toString()
    var pswd1 = edtPswd1?.text.toString()
    var pswd2 = edtPswd2?.text.toString()

    if (name.isEmpty()) {
        edtName?.setError("Enter Name")
        edtName?.requestFocus()
        boolean = false
         }
    else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches())
    {
        edtEmailid?.setError("Enter valid email")
        edtEmailid?.requestFocus()
        boolean = false
    }
   else if (!android.util.Patterns.PHONE.matcher(phoneNo)
            .matches())
    {
        edtPhone?.setError("Enter valid Mobile number")
        edtPhone?.requestFocus()
        boolean = false
    }
    else if (location.isEmpty()) {
        edtLocation?.setError("Enter Location")
        edtLocation?.requestFocus()
        boolean = false
    }

    else if (pswd1.isEmpty()) {
        edtPswd1?.setError("Enter Password")
        edtPswd1?.requestFocus()

        boolean = false
    }
    else if (pswd2.isEmpty()) {
        edtPswd2?.setError("Confirm Password")
        edtPswd2?.requestFocus()
        boolean = false
    }
    else if (!pswd1.equals(pswd2)) {
        Toast.makeText(this, " password mismatched", Toast.LENGTH_SHORT).show()
        edtPswd2?.setError("Password Mismatched")
        edtPswd2?.requestFocus()
        boolean = false

    }
  /*  else if(pswd1.equals(pswd2)) {
        Toast.makeText(this, "Please Accept T&C", Toast.LENGTH_SHORT).show()
        chkTc?.setOnCheckedChangeListener { _, isChecked ->
            (if (!isChecked) {
                Toast.makeText(this, "Don't Signup", Toast.LENGTH_SHORT).show()
                btnSignup?.setError("Accept Terms & Conditions")
                btnSignup?.requestFocus()
                boolean = false

            } else {
                Toast.makeText(this, "Do Signup", Toast.LENGTH_SHORT).show()

            })
        }
    }*/

    return boolean
}
    private   fun pswdactivity() {
        val checkBox = findViewById<CheckBox>(R.id.terms_conditions)
        checkBox?.setOnCheckedChangeListener { _, isChecked ->
            (if (isChecked) {
                edtPswd1?.inputType = InputType.TYPE_CLASS_TEXT
                edtPswd2?.inputType = InputType.TYPE_CLASS_TEXT
                chkTc?.setText(R.string.hide_pwd)

            } else {
                edtPswd1?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                edtPswd2?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                chkTc?.setText(R.string.show_pwd)

            })
        }

    }

    private fun datavalidation()
    {

       var Email=edtEmailid?.text.toString()
        var Name=edtName?.text.toString()
        var Phone=edtPhone?.text.toString()
        var Location=edtLocation?.text.toString()
        var Password=edtPswd1?.text.toString()
        var Id=Name.hashCode().toString()

        ApiService.loginApiCall().doRegister(Id,Name,Email,Phone,Location,Password)
        .enqueue(object : Callback<Message> {
            override fun onResponse(call: Call<Message>, response: Response<Message>)
            {
                Log.e("pkResponse::::", response.body().toString())
                val messages :Message
                var signResponse = response.body()!!
                if (signResponse.status)
                {
                    Toast.makeText(applicationContext, "Please Do Login", Toast.LENGTH_LONG).show()
                    var i = Intent(applicationContext, Login::class.java)
                    startActivity(i)

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

}