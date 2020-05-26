package hu.bme.aut.android.tourguide

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class ProfileFragment: Fragment() {
    private val TAG = "ProfileFragment"

    private lateinit var etName: EditText
    private lateinit var btnEdit: Button
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var cbPassword: CheckBox
    private lateinit var etNewPassword: EditText
    private lateinit var etNewPasswordAgain: EditText
    private lateinit var etOldPassword: EditText
    private lateinit var tvCities: TextView
    private var isInEditMode: Boolean = false

    private var auth = FirebaseAuth.getInstance()
    private val database = Firebase.database.reference

    private var user = User()
    private lateinit var newPassAgain: String

    private val PREFS_FILENAME = "hu.bme.aut.android.tourguide.mypreference"
    private val PASSWORD = "userPassword"
    private val EMAIL= "userEmail"
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        user = arguments!!.getSerializable("user") as User

        etName = view.findViewById(R.id.et_profile_name)
        btnEdit = view.findViewById(R.id.btn_profile_edit)
        etEmail = view.findViewById(R.id.et_profile_email)
        etPhone = view.findViewById(R.id.et_profile_phone)
        cbPassword = view.findViewById(R.id.cb_profile_password)
        etNewPassword = view.findViewById(R.id.et_profile_new_password)
        etNewPasswordAgain = view.findViewById(R.id.et_profile_new_password_again)
        etOldPassword = view.findViewById(R.id.et_profile_old_password)
        tvCities = view.findViewById(R.id.tv_profile_cities)

        prefs = this.activity!!.getSharedPreferences(PREFS_FILENAME, 0)

        etName.setText(user.name)
        etEmail.setText(user.email)
        etPhone.setText(user.phoneNumber)

        fillTextView()

        btnEdit.setOnClickListener {
            if(!isInEditMode) {
                btnEdit.setBackgroundResource(R.drawable.ic_save_black_24dp)
                changeEditTexts(true)
            }else{
                val newName = etName.text.toString()
                val newPhone = etPhone.text.toString()
                val newEmail = etEmail.text.toString()

                val isNameNew = (newName != user.name)
                val isPhoneNew = (newPhone != user.phoneNumber)
                val isEmailNew = (newEmail != user.email)
                val isCheckBox = cbPassword.isChecked

                val isAnythingNew = (isNameNew || isEmailNew || isPhoneNew || isCheckBox)

                if(isAnythingNew){
                    val oldPassword = etOldPassword.text.toString()
                    if(oldPassword == prefs.getString(PASSWORD,null)!!) {
                        var newPassword = ""
                        if (isCheckBox) {
                            newPassword = etNewPassword.text.toString()
                            newPassAgain = etNewPasswordAgain.text.toString()

                            val newPassValidity = isValidPassword(newPassword)
                            val newPassAgainValidity = isValidRepeatPassword(newPassword, newPassAgain)

                            if (newPassValidity != "OK") {
                                etNewPassword.error = newPassValidity
                                etNewPassword.requestFocus()
                                return@setOnClickListener
                            }
                            if (newPassAgainValidity != "OK") {
                                etNewPasswordAgain.error = newPassAgainValidity
                                etNewPasswordAgain.requestFocus()
                                return@setOnClickListener
                            }
                        }
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Modifying profile data")
                        builder.setMessage("Do you really want to change your profile details?")
                        builder.setPositiveButton("YES") { _, _ ->
                            if (isCheckBox) {
                                updatePassword(newPassword)
                            }
                            if (isEmailNew) {
                                updateUserEmail(newEmail)
                            }
                            if (isNameNew || isPhoneNew) {
                                updateUserDatabase(newName, newPhone)
                            }
                            Toast.makeText(activity, "Ok, we save your modifications!", Toast.LENGTH_SHORT).show()
                            btnEdit.setBackgroundResource(R.drawable.ic_edit_black_24dp)
                            changeEditTexts(false)
                            etNewPassword.isVisible = false
                            etNewPasswordAgain.isVisible = false
                        }
                        builder.setNegativeButton("NO") { _, _ ->
                            Toast.makeText(activity, "Nothing will be changed!.", Toast.LENGTH_SHORT).show()
                        }
                        builder.setNeutralButton("Cancel") { _, _ ->
                            Toast.makeText(activity, "You cancelled the modifications!", Toast.LENGTH_SHORT).show()
                            etEmail.setText(user.email)
                            etName.setText(user.name)
                            etPhone.setText(user.phoneNumber)
                            btnEdit.setBackgroundResource(R.drawable.ic_edit_black_24dp)
                            changeEditTexts(false)
                        }
                        val dialog = builder.create()

                        dialog.show()
                    }else{
                        etOldPassword.error = "Wrong current password!"
                        etOldPassword.requestFocus()
                    }
                }else{
                    btnEdit.setBackgroundResource(R.drawable.ic_edit_black_24dp)
                    changeEditTexts(false)
                }
            }
        }

        cbPassword.setOnCheckedChangeListener { _, isChecked ->
                etNewPassword.isVisible = isChecked
                etNewPasswordAgain.isVisible = isChecked
        }

        tvCities.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from", "ProfF")
            val fragmentDial = CitiesDialogFragment()
            fragmentDial.arguments = bundle
            fragmentDial.show(activity!!.supportFragmentManager,"TAG")
        }
        return view
    }

    private fun updateUserEmail(newEmail: String){
        database.child("users").child(user.uid).child("email").setValue(newEmail)
        val credential = EmailAuthProvider.getCredential(
            prefs.getString(EMAIL,null)!!,
            prefs.getString(PASSWORD,null)!!
        )
        auth.currentUser!!.reauthenticate(credential)
            .addOnCompleteListener{firstAuthenticationTask ->
                if(firstAuthenticationTask.isSuccessful){
                    Log.d(TAG,"Successfully authentication!")
                    auth = FirebaseAuth.getInstance()
                    auth.currentUser!!.updateEmail(newEmail)
                        .addOnCompleteListener{updateEmailTask->
                            if(updateEmailTask.isSuccessful){
                                Log.d(TAG,"Successfully saved new email")
                            }else{
                                Log.d(TAG,"Failure during saving new email", updateEmailTask.exception)
                            }
                        }
                    auth = FirebaseAuth.getInstance()
                    val newCredential = EmailAuthProvider.getCredential(
                        prefs.getString(EMAIL,null)!!,
                        prefs.getString(PASSWORD,null)!!
                    )
                    auth.currentUser!!.reauthenticate(newCredential)
                        .addOnCompleteListener{secondAuthenticationTask->
                            if(secondAuthenticationTask.isSuccessful){
                                Log.d(TAG,"Successfully second authentication!")
                                auth.currentUser!!.sendEmailVerification()
                                    .addOnCompleteListener{sendEmailVerificationTask ->
                                        if(sendEmailVerificationTask.isSuccessful){
                                            Log.d(TAG,"Successfully sent email!")
                                        }else{
                                            Log.d(TAG,"Failure during sending email!", sendEmailVerificationTask.exception)
                                        }
                                    }
                            }else{
                                Log.d(TAG,"Failure during second authentication", secondAuthenticationTask.exception)
                            }
                        }
                }else{
                    Log.d(TAG,"Failure during authentication", firstAuthenticationTask.exception)
                }
            }
        val editor = prefs.edit()
        editor.putString(EMAIL, newEmail)
        editor.apply()
    }

    private fun updateUserDatabase(newName: String, newPhoneNumber: String){
        database.child("users").child(user.uid).child("name").setValue(newName)
        database.child("users").child(user.uid).child("phoneNumber").setValue(newPhoneNumber)
    }

    private fun updatePassword(newPassword: String){
        val credential = EmailAuthProvider.getCredential(
            prefs.getString(EMAIL, null)!!,
            prefs.getString(PASSWORD, null)!!
        )
        auth.currentUser!!.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    database.child("users").child(user.uid).child("password").setValue(newPassword)
                    val editor = prefs.edit()
                    editor.putString(PASSWORD, newPassword)
                    editor.apply()
                    auth = FirebaseAuth.getInstance()
                    auth.currentUser!!.updatePassword(newPassword)
                    (activity as NavigationActivity).menuItemsPassword()
                }
            }
    }

    private fun changeEditTexts(bool: Boolean){
        etEmail.isEnabled = bool
        etName.isEnabled = bool
        etPhone.isEnabled = bool
        isInEditMode = bool
        cbPassword.isVisible = bool
        etOldPassword.isVisible = bool
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

    fun fillTextView() {
        var temp = ""
        user.cities.clear()
        for(city in (activity as NavigationActivity).cityList){
            if(city.isSelected){
                temp += " ${city.name}"
                user.cities.add(city)
            }
        }
        if(temp != ""){
            temp.removePrefix("")
        }else{
            temp = "You haven\'t chosen any city yet"
        }
        tvCities.text = temp
        database.child("users").child(user.uid).child("cities").setValue(user.cities)
    }
}
