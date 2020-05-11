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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern


class ProfileFragment: Fragment() {

    private lateinit var etName: EditText
    private lateinit var btnEdit: Button
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var cbPassword: CheckBox
    private lateinit var etNewPassword: EditText
    private lateinit var etNewPasswordAgain: EditText
    private lateinit var etOldPassword: EditText
    private lateinit var tvChange: TextView
    private lateinit var tvCities: TextView
    private var isInEditMode: Boolean = false

    private val uid = FirebaseAuth.getInstance().uid
    private val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
    private lateinit var auth: FirebaseAuth
    private val database = Firebase.database.reference

    lateinit var originalName: String
    lateinit var originalPhone: String
    var userCitiesString = ""
    var userCityList = mutableListOf<String>()
    lateinit var newPass: String
    lateinit var newPassAgain: String

    val PREFS_FILENAME = "hu.bme.aut.android.tourguide.mypreference"
    val PASSWORD = "userPassword"
    val EMAIL= "userEmail"
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        etName = view.findViewById(R.id.et_profile_name)
        btnEdit = view.findViewById(R.id.btn_profile_edit)
        etEmail = view.findViewById(R.id.et_profile_email)
        etPhone = view.findViewById(R.id.et_profile_phone)
        cbPassword = view.findViewById(R.id.cb_profile_password)
        etNewPassword = view.findViewById(R.id.et_profile_new_password)
        etNewPasswordAgain = view.findViewById(R.id.et_profile_new_password_again)
        etOldPassword = view.findViewById(R.id.et_profile_old_password)
        tvChange = view.findViewById(R.id.tv_profile_change)
        tvCities = view.findViewById(R.id.tv_profile_cities)

        auth = FirebaseAuth.getInstance()

        prefs = this.activity!!.getSharedPreferences(PREFS_FILENAME, 0)

        ref.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(de: DatabaseError) {
            }
            override fun onDataChange(ds: DataSnapshot) {
                originalName = ds.child("name").value.toString()
                originalPhone = ds.child("phoneNumber").value.toString()
                userCitiesString = ds.child("cities").value.toString()
                etName.setText(originalName)
                etPhone.setText(originalPhone)
                if(userCitiesString != "") {
                    userCitiesString = userCitiesString.removePrefix(" ")
                    tvCities.text = userCitiesString
                    val list = userCitiesString.split(" ")
                    userCityList = list.toMutableList()
                    for(cityS in userCityList){
                        if(activity != null){
                            for(city in (activity as NavigationActivity).cityList){
                                if(city.name == cityS){
                                    city.isSelected = true
                                }
                            }
                        }
                    }
                }
            }
        })

        changeEditTexts(false)
        etNewPassword.isVisible = false
        etNewPasswordAgain.isVisible = false
        var originalEmail = prefs.getString(EMAIL,null)
        etEmail.setText(originalEmail)
        btnEdit.setOnClickListener {
            if(!isInEditMode) {
                btnEdit.setBackgroundResource(R.drawable.ic_save_black_24dp)
                changeEditTexts(true)
            }else{
                originalEmail = prefs.getString(EMAIL,null)
                val newName = etName.text.toString()
                val newPhone = etPhone.text.toString()
                val newEmail = etEmail.text.toString()

                val isNameNew = (newName != originalName)
                val isPhoneNew = (newPhone != originalPhone)
                val isEmailNew = (newEmail != originalEmail)
                val isCheckBox = cbPassword.isChecked
                val isAnythingNew = (isNameNew || isEmailNew || isPhoneNew || isCheckBox)

                if(isAnythingNew){
                    val oldPassword = etOldPassword.text.toString()
                    if(oldPassword == prefs.getString(PASSWORD,null)!!) {
                        if (isCheckBox) {
                            newPass = etNewPassword.text.toString()
                            newPassAgain = etNewPasswordAgain.text.toString()

                            val newPassValidity = isValidPassword(newPass)
                            val newPassAgainValidity = isValidRepeatPassword(newPass, newPassAgain)

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
                        builder.setMessage("Do you really want to change some profile details?")
                        builder.setPositiveButton("YES") { _, _ ->
                            if (isCheckBox) {
                                val credential = EmailAuthProvider.getCredential(
                                    prefs.getString(EMAIL, null)!!,
                                    prefs.getString(PASSWORD, null)!!
                                )
                                auth.currentUser!!.reauthenticate(credential)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            database.child("users").child(uid.toString())
                                                .child("password").setValue(newPass)
                                            val editor = prefs.edit()
                                            editor.putString(PASSWORD, newPass)
                                            editor.apply()
                                            auth = FirebaseAuth.getInstance()
                                            auth.currentUser!!.updatePassword(newPass)
                                            (activity as NavigationActivity).menuItemsPassword()
                                        }
                                    }
                            }
                            if (isEmailNew) {
                                updateUserEmail(newEmail)
                            }
                            if (isNameNew || isPhoneNew) {
                                updateUserDatabase(newName, newPhone)
                            }
                            Toast.makeText(
                                activity,
                                "Ok, we save your modifications!",
                                Toast.LENGTH_LONG
                            ).show()
                            btnEdit.setBackgroundResource(R.drawable.ic_edit_black_24dp)
                            changeEditTexts(false)
                            etNewPassword.isVisible = false
                            etNewPasswordAgain.isVisible = false
                        }
                        builder.setNegativeButton("NO") { _, _ ->
                            Toast.makeText(
                                activity,
                                "Nothing will be changed!.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        builder.setNeutralButton("Cancel") { _, _ ->
                            Toast.makeText(
                                activity,
                                "You cancelled the modifications!",
                                Toast.LENGTH_SHORT
                            ).show()
                            etEmail.setText(originalEmail)
                            etName.setText(originalName)
                            etPhone.setText(originalPhone)
                            btnEdit.setBackgroundResource(R.drawable.ic_edit_black_24dp)
                            changeEditTexts(false)
                        }
                        val dialog = builder.create()

                        dialog.show()
                    }else{
                        Toast.makeText(activity, "Wrong current password!", Toast.LENGTH_SHORT).show()
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

        tvChange.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("from", "ProfF")
            val fragmentDial = CitiesDialogFragment()
            fragmentDial.arguments = bundle
            fragmentDial.show(activity!!.supportFragmentManager,"TAG")
        }

        return view
    }

    private fun updateUserEmail(newEmail: String){
        database.child("users").child(uid.toString()).child("email").setValue(newEmail)
        val credential = EmailAuthProvider.getCredential(prefs.getString(EMAIL,null)!!, prefs.getString(PASSWORD,null)!!)
        auth.currentUser!!.reauthenticate(credential)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Log.d("ProfileFragment","Successfully reaut")
                    auth = FirebaseAuth.getInstance()
                    auth.currentUser!!.updateEmail(newEmail)
                        .addOnCompleteListener{lol->
                            if(lol.isSuccessful){Log.d("ProfileFragment","Successfully save email")
                            }else{
                                Log.d("ProfileFragment","Failure save email", lol.exception)
                            }
                        }
                    auth = FirebaseAuth.getInstance()
                    val newcredential = EmailAuthProvider.getCredential(prefs.getString(EMAIL,null)!!, prefs.getString(PASSWORD,null)!!)
                    auth.currentUser!!.reauthenticate(newcredential)
                        .addOnCompleteListener{ti->
                            if(ti.isSuccessful){
                                Log.d("ProfileFragment","Successfully second aut")
                                auth.currentUser!!.sendEmailVerification()
                                    .addOnCompleteListener{task ->
                                        if(task.isSuccessful){
                                            Log.d("ProfileFragment","Successfully send email!")
                                        }else{
                                            Log.d("ProfileFragment","Failure send email!", task.exception)
                                        }
                                    }
                            }else{
                                Log.d("ProfileFragment","Failure failed second aut", ti.exception)
                            }
                        }
                }else{
                    Log.d("ProfileFragment","Failure reaut", it.exception)
                }
            }
        val editor = prefs.edit()
        editor.putString(EMAIL, newEmail)
        editor.apply()
    }

    private fun updateUserDatabase(newName: String, newPhone: String){
        database.child("users").child(uid.toString()).child("name").setValue(newName)
        database.child("users").child(uid.toString()).child("phoneNumber").setValue(newPhone)
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
        for(city in (activity as NavigationActivity).cityList){
            if(city.isSelected){
                temp += " ${city.name}"
            }
        }
        if(temp != ""){
            temp.removePrefix("")
            tvCities.text = temp
        }else{
            tvCities.text = "You haven\'t chosen any city yet"
        }
        database.child("users").child(uid.toString()).child("cities").setValue(temp)
    }
}
