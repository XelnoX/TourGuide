package hu.bme.aut.android.tourguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot.*
import kotlinx.android.synthetic.main.activity_login.*

class ForgotActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        val contentView = findViewById<View>(R.id.forgot_activity)
        contentView.setOnTouchListener { v, _ ->
            v?.hideKeyboard()
            true
        }

        auth = FirebaseAuth.getInstance()
        this.supportActionBar?.hide()

        tv_forgot_back.setOnClickListener {
            super.onBackPressed()
        }

        btn_forgot.setOnClickListener {
            val email = et_forgot_email.text.toString().trim()

            if(TextUtils.isEmpty(email)){
                et_forgot_email.error = "Email required"
                et_forgot_email.requestFocus()
                return@setOnClickListener
            }else{
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener{task ->
                        if (task.isSuccessful) {
                            Toast.makeText(applicationContext, "Check email to reset your password!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Failed to send reset password email!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun View.hideKeyboard() {
        val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}
