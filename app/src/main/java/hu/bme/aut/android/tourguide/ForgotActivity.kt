package hu.bme.aut.android.tourguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot.*
import kotlinx.android.synthetic.main.activity_login.*

class ForgotActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        auth = FirebaseAuth.getInstance()
        this.supportActionBar?.hide()

        tv_forgot_back.setOnClickListener {
            super.onBackPressed()
        }

        btn_forgot.setOnClickListener {
            val email = et_forgot_email.text.toString().trim()

            if(TextUtils.isEmpty(email)){
                et_email.error = "Email required"
                et_email.requestFocus()
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
}
