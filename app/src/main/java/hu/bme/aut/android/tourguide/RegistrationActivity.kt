package hu.bme.aut.android.tourguide

import android.content.Intent
import android.icu.util.BuddhistCalendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registration.*
import android.util.Log
import com.google.firebase.database.*
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var myRef: DatabaseReference
    var cityList = mutableListOf<City>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        myRef = FirebaseDatabase.getInstance().getReference("cities")

        auth = FirebaseAuth.getInstance()
        this.supportActionBar?.hide()

        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(snap: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(snap: DataSnapshot) {
                for(dataSnap in snap.children){
                    val city = dataSnap.getValue(City::class.java)
                    if(city != null){
                        cityList.add(city)
                    }
                }
            }

        })

        tv_reg_cities.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from", "RegA")
            val fragmentDial = CitiesDialogFragment()
            fragmentDial.arguments = bundle
            fragmentDial.show(supportFragmentManager,"TAG")
        }

        val contentView: View = findViewById(R.id.registration_activity)
        contentView.setOnTouchListener { v, _ ->
            v?.hideKeyboard()
            true
        }

        tv_reg_cancel.setOnClickListener {
            super.onBackPressed()
        }

        btn_reg_register.setOnClickListener {
            val name = et_reg_name.text.toString()
            val phone = et_reg_phone.text.toString()
            val email = et_reg_email.text.toString()
            val password = et_reg_password.text.toString()
            val repeatPassword = et_reg_repeat_password.text.toString()

            val nameValidity = isValidName(name)
            val phoneValidity = isValidPhoneNumber(phone)
            val emailValidity = isValidEmail(email)
            val passwordValidity = isValidPassword(password)
            val repeatPasswordValidity = isValidRepeatPassword(password, repeatPassword)

            if(nameValidity != "OK"){
                et_reg_name.error = nameValidity
                et_reg_name.requestFocus()
                return@setOnClickListener
            }
            if(phoneValidity != "OK"){
                et_reg_phone.error = phoneValidity
                et_reg_phone.requestFocus()
                return@setOnClickListener
            }
            if(emailValidity != "OK"){
                et_reg_email.error = emailValidity
                et_reg_email.requestFocus()
                return@setOnClickListener
            }
            if(passwordValidity != "OK"){
                et_reg_password.error = passwordValidity
                et_reg_password.requestFocus()
                return@setOnClickListener
            }
            if(repeatPasswordValidity != "OK"){
                et_reg_repeat_password.error = repeatPasswordValidity
                et_reg_repeat_password.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { aTask ->
                    if (aTask.isSuccessful) {
                        Log.d("RegisterActivity", "Successfully created user!")
                        saveUserToFirebase(name, phone ,email, password)
                        Toast.makeText(applicationContext, "You have successfully registered!", Toast.LENGTH_LONG).show()

                        auth.currentUser!!.sendEmailVerification()
                            .addOnCompleteListener(this) {bTask ->
                                if (bTask.isSuccessful){
                                    auth.signOut()
                                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(applicationContext, "Email verification was failed!", Toast.LENGTH_LONG).show()
                                }
                            }

                    } else {
                        Log.w("RegisterActivity", "Failure during creating user!", aTask.exception)
                        Toast.makeText(applicationContext, "Registration failed!", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    fun fillTexView() {
        var temp = ""
        for(city in cityList){
            if(city.isSelected){
                temp += " ${city.name}"
            }
        }
        if(temp != ""){
            tv_reg_cities.text = temp
        }

    }

    private fun listSelectedCities(list: MutableList<City>): String{
        var cities = ""
        for(city in list){
            if(city.isSelected){
                cities += " ${city.name}"
            }
        }
        return cities
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun isValidName(name: String): String{
        return if(name.isEmpty()){
            "Name required!"
        }else{
            "OK"
        }
    }

    private fun isValidPhoneNumber(phone: String): String{
        return if(TextUtils.isEmpty(phone)){
            "Phone number required!"
        }else if(!android.util.Patterns.PHONE.matcher(phone).matches()){
            "Use a valid phone number!"
        }else{
            "OK"
        }
    }

    private fun isValidEmail(email: String): String {
        return if(TextUtils.isEmpty(email)){
            "Email required!"
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            "Use a valid email! (Like: example@example.com)!"
        }else{
            "OK"
        }
    }

    private fun isValidPassword(password: String): String {
        if(password.length < 8){
            return "Password should be minimum 8 characters long!"
        }
        var exp = ".*[0-9].*"
        var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
        var match = pattern.matcher(password)
        if(!match.matches()){
            return "Password should contain at least one number!"
        }
        exp = ".*[A-Z].*"
        pattern = Pattern.compile(exp)
        match = pattern.matcher(password)
        if (!match.matches()) {
            return "Password should contain at least one capital letter!"
        }
        exp = ".*[a-z].*"
        pattern = Pattern.compile(exp)
        match = pattern.matcher(password)
        if (!match.matches()) {
            return "Password should contain at least one small letter!"
        }
        return "OK"
    }

    private fun isValidRepeatPassword(password: String, repeatPassword: String):String{
        return when {
            repeatPassword.isEmpty() -> {
                "Password required!"
            }
            password != repeatPassword -> {
                "Passwords are't the same!"
            }
            else -> {
                "OK"
            }
        }
    }

    private fun saveUserToFirebase(name: String, phone: String ,email: String, password: String){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")

        val user = User(uid!!, phone, name, email, password, listSelectedCities(cityList))

        ref.setValue(user)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("RegisterActivity", "Successfully saved user!")
                } else {
                    Log.w("RegisterActivity", "Failure during saving user!", task.exception)
                }
            }
    }
}
