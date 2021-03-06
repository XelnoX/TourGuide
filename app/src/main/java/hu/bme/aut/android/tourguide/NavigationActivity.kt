package hu.bme.aut.android.tourguide

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.regex.Pattern

class NavigationActivity : AppCompatActivity() {
    private val TAG = "NavigationActivity"

    private lateinit var password: String
    private val PREFS_FILENAME = "hu.bme.aut.android.tourguide.mypreference"
    private val PASSWORD = "userPassword"
    private lateinit var navView: BottomNavigationView
    private lateinit var currentFragment: Fragment
    var cityList = mutableListOf<City>()
    var routeList = mutableListOf<Route>()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val cityRef = firebaseDatabase.getReference("cities")
    private val routeRef = firebaseDatabase.getReference("routes")
    private val uid = FirebaseAuth.getInstance().uid
    private val userRef = firebaseDatabase.getReference("users/$uid")
    private val userCitiesRef = firebaseDatabase.getReference("users/$uid").child("cities")

    private val user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        Toast.makeText(applicationContext, "Welcome!", Toast.LENGTH_SHORT).show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        navView = findViewById(R.id.bottom_navigation_view)

        menuItemsPassword()

        user.uid = uid!!

        replaceFragment(LoadingFragment())

        userRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failure during getting user data!")
            }

            override fun onDataChange(snap: DataSnapshot) {
                Log.d(TAG, "User data has been gotten!")
                user.name = snap.child("name").value.toString()
                user.email = snap.child("email").value.toString()
                user.phoneNumber = snap.child("phoneNumber").value.toString()
                user.password = snap.child("password").value.toString()
            }

        })
        userCitiesRef.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failure during getting user cities!")
            }

            override fun onDataChange(snap: DataSnapshot) {
                Log.d(TAG, "User cities has been gotten!")
                for(dataSnap in snap.children){
                    val city = dataSnap.getValue(City::class.java)
                    if(city != null){
                        user.cities.add(city)
                    }
                }
                if(user.cities.isNotEmpty()){
                    resetCityList()
                }
            }

        })

        cityRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failure during getting list of cities!", error.toException())
            }

            override fun onDataChange(snap: DataSnapshot) {
                Log.d(TAG, "Successfully got list of cities!")
                for(dataSnap in snap.children){
                    val city = dataSnap.getValue(City::class.java)
                    if(city != null){
                        cityList.add(city)
                    }
                }
                if(user.cities.isNotEmpty()){
                    resetCityList()
                }
            }
        })

        routeRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failure during getting list of routes!", error.toException())
            }

            override fun onDataChange(snap: DataSnapshot) {
                Log.d(TAG, "Successfully got list of routes!")
                for(dataSnap in snap.children){
                    val route = dataSnap.getValue(Route::class.java)
                    if(route != null){
                        routeList.add(route)
                    }
                }
                if(isPasswordStrong()){
                    addToBackStackAndReplaceFragmentAndGiveUser(RoutesFragment())
                }else{
                    Toast.makeText(applicationContext, "Your password is weak, please change it!", Toast.LENGTH_SHORT).show()
                    replaceFragmentAndGiveUser(ProfileFragment())
                }
            }
        })

        val navigation: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navigation.setOnNavigationItemSelectedListener(onNavClick)
    }

    private val onNavClick = BottomNavigationView.OnNavigationItemSelectedListener {item->
            when(item.itemId){
                R.id.navigation_profile ->{
                    Log.d(TAG, "ProfileFragment is on the top!")
                    replaceFragmentAndGiveUser(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_tours ->{
                    Log.d(TAG, "RoutesFragment is on the top!")
                    addToBackStackAndReplaceFragmentAndGiveUser(RoutesFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_map ->{
                    Log.d(TAG, "MapFragment is on the top!")
                    replaceFragment(MapFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
        return@OnNavigationItemSelectedListener true
    }

    fun addToBackStackAndReplaceFragment(fragment: Fragment){
        currentFragment = fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, fragment)
        fragmentTransaction.addToBackStack("fragment")
        fragmentTransaction.commit()
    }
    fun addToBackStackAndReplaceFragmentAndGiveUser(fragment: Fragment){
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        fragment.arguments = bundle
        addToBackStackAndReplaceFragment(fragment)
    }

    fun replaceFragment(fragment: Fragment){
        currentFragment = fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_holder, fragment)
        fragmentTransaction.commit()
    }
    private fun replaceFragmentAndGiveUser(fragment: Fragment){
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    fun resetCityList(){
        for(city in cityList){
            city.isSelected = false
        }
        for(UserCity in user.cities){
            for (NavCity in cityList){
                if(UserCity.name == NavCity.name){
                    NavCity.isSelected = true
                }
            }
        }
    }

    fun getList() : MutableList<City>{
        return mutableListOf<City>().apply { addAll(cityList) }
    }

    private fun isPasswordStrong(): Boolean{
        if(password.length < 8){
            return false
        }
        var exp = ".*[0-9].*"
        var pattern = Pattern.compile(exp, Pattern.CASE_INSENSITIVE)
        var match = pattern.matcher(password)
        if(!match.matches()){
            return false
        }
        exp = ".*[A-Z].*"
        pattern = Pattern.compile(exp)
        match = pattern.matcher(password)
        if (!match.matches()) {
            return false
        }
        exp = ".*[a-z].*"
        pattern = Pattern.compile(exp)
        match = pattern.matcher(password)
        if (!match.matches()) {
            return false
        }
        return true
    }

    fun menuItemsPassword(){
        val prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
        password = prefs.getString(PASSWORD,"")!!
        navView.menu.findItem(R.id.navigation_tours).isEnabled = isPasswordStrong()
        navView.menu.findItem(R.id.navigation_map).isEnabled = isPasswordStrong()
    }
}
