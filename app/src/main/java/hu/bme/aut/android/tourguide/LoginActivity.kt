package hu.bme.aut.android.tourguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val PREFS_FILENAME = "hu.bme.aut.android.tourguide.mypreference"
    private val PASSWORD = "userPassword"
    private val EMAIL= "userEmail"
    private val TAG = "LoginActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            Thread.sleep(1000)
        }catch (e: InterruptedException ){
            e.printStackTrace()
        }

        val prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        auth = FirebaseAuth.getInstance()

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val contentView: View = findViewById(R.id.login_activity)
        contentView.setOnTouchListener { v, _ ->
            v?.hideKeyboard()
            true
        }

        btn_login.setOnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()

            if(email.isEmpty()){
                et_email.error = "Email required"
                et_email.requestFocus()
                return@setOnClickListener
            }
            if(password.isEmpty()){
                et_password.error = "Password required"
                et_password.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        if(auth.currentUser!!.isEmailVerified) {
                            Log.d(TAG, "User successfully logged in!")
                            Toast.makeText(applicationContext, "Welcome!", Toast.LENGTH_SHORT).show()

                            val editor = prefs.edit()
                            editor.putString(PASSWORD, password)
                            editor.putString(EMAIL, email)
                            editor.apply()

                            val intent = Intent(this@LoginActivity, NavigationActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }else{
                            Log.d(TAG, "Email, hasn't been verified!")
                            Toast.makeText(applicationContext, "Email hasn't been verified!", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Log.d(TAG, "Failure during logging in!", task.exception)
                        Toast.makeText(applicationContext, "Wrong username or password!", Toast.LENGTH_LONG).show()
                    }
                }
        }

        tv_register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        tv_forgot_password.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotActivity::class.java)
            startActivity(intent)
        }

        tv_login_quit.setOnClickListener {
            moveTaskToBack(true)
            exitProcess(-1)
        }
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
